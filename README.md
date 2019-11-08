## 実験手順

### Prerequisites
- kubernetes
  - v1.14
- skaffold
  - v1.0.0

### 1. プログラムを走らせる.

```
$ skaffold dev --port-forward
```

[補足] クラスターが立ち上がってから、
```
while true
   curl http://localhost:9000/
   sleep 1
   echo
end
```
のようなシェルスクリプトで、実験のあいだ中にHTTPリクエストをかけ続けるべきかもしれない。
このリクエストの有無で結果が変わる場合があった。理由は不明。

### 2. Podが立ち上がっているか確認する.

```
$ kubectl get pods -o wide --sort-by=.status.podIP
NAME                                    READY   STATUS    RESTARTS   AGE    IP          NODE             NOMINATED NODE   READINESS GATES
akka-cluster-operator-bb5b5985f-bh72w   1/1     Running   0          112s   10.1.3.52   docker-desktop   <none>           <none>
akka-cluster-test-6ff97cfbdb-6htlc      1/1     Running   0          107s   10.1.3.53   docker-desktop   <none>           <none>
akka-cluster-test-6ff97cfbdb-f25zl      1/1     Running   0          107s   10.1.3.54   docker-desktop   <none>           <none>
akka-cluster-test-6ff97cfbdb-zdxv7      1/1     Running   0          107s   10.1.3.55   docker-desktop   <none>           <none>
akka-cluster-test-6ff97cfbdb-684p6      1/1     Running   0          107s   10.1.3.56   docker-desktop   <none>           <none>
akka-cluster-test-6ff97cfbdb-4grn5      1/1     Running   0          107s   10.1.3.57   docker-desktop   <none>           <none>
```

### 3. 5つのAkkaクラスタのノードが2, 3に分かれるよう、ネットワークを分割する.
```
$ python3 tools/split_network.py | sh
```

ちなみに上記のプログラムは、下記のようなコマンド列を出力するものである.
```
$ python3 tools/split_network.py
kubectl exec akka-cluster-test-6ff97cfbdb-6htlc -- route add -host 10.1.3.55 reject
kubectl exec akka-cluster-test-6ff97cfbdb-6htlc -- route add -host 10.1.3.56 reject
kubectl exec akka-cluster-test-6ff97cfbdb-6htlc -- route add -host 10.1.3.57 reject
kubectl exec akka-cluster-test-6ff97cfbdb-f25zl -- route add -host 10.1.3.55 reject
kubectl exec akka-cluster-test-6ff97cfbdb-f25zl -- route add -host 10.1.3.56 reject
kubectl exec akka-cluster-test-6ff97cfbdb-f25zl -- route add -host 10.1.3.57 reject
kubectl exec akka-cluster-test-6ff97cfbdb-zdxv7 -- route add -host 10.1.3.53 reject
kubectl exec akka-cluster-test-6ff97cfbdb-684p6 -- route add -host 10.1.3.53 reject
kubectl exec akka-cluster-test-6ff97cfbdb-4grn5 -- route add -host 10.1.3.53 reject
kubectl exec akka-cluster-test-6ff97cfbdb-zdxv7 -- route add -host 10.1.3.54 reject
kubectl exec akka-cluster-test-6ff97cfbdb-684p6 -- route add -host 10.1.3.54 reject
kubectl exec akka-cluster-test-6ff97cfbdb-4grn5 -- route add -host 10.1.3.54 reject
```

### 4. しばらく待つと下記のような状態になる.

※以降、https://github.com/akka-cluster-custom-downing/akka-cluster-custom-downing/pull/3 でのバグ修正後の結果を述べる。

```
$ kubectl get pods -o wide --sort-by=.status.podIP
NAME                                     READY   STATUS    RESTARTS   AGE     IP           NODE             NOMINATED NODE   READINESS GATES
akka-cluster-operator-68c785666f-7v7s5   1/1     Running   0          2m16s   10.1.3.149   docker-desktop   <none>           <none>
akka-cluster-test-7df88b956f-4bqlc       1/1     Running   0          2m10s   10.1.3.150   docker-desktop   <none>           <none>
akka-cluster-test-7df88b956f-p44xk       1/1     Running   0          2m10s   10.1.3.151   docker-desktop   <none>           <none>
akka-cluster-test-7df88b956f-24q7h       0/1     Running   1          2m10s   10.1.3.152   docker-desktop   <none>           <none>
akka-cluster-test-7df88b956f-4frhv       0/1     Running   1          2m10s   10.1.3.153   docker-desktop   <none>           <none>
akka-cluster-test-7df88b956f-4n9qz       0/1     Running   1          2m10s   10.1.3.154   docker-desktop   <none>           <none>
```

```
$ kubectl get akkaclusters -o yaml
(略)
  status:
    cluster:
      leader: akka.tcp://akka-cluster-test@10.1.3.150:2552
      members:
      - node: akka.tcp://akka-cluster-test@10.1.3.150:2552
        roles:
        - dc-default
        status: Up
      - node: akka.tcp://akka-cluster-test@10.1.3.151:2552
        roles:
        - dc-default
        status: Up
      oldest: akka.tcp://akka-cluster-test@10.1.3.150:2552
      oldestPerRole:
        dc-default: akka.tcp://akka-cluster-test@10.1.3.150:2552
      unreachable: []
    lastUpdate: "2019-11-11T01:38:36Z"
    managementHost: 10.1.3.150
    managementPort: 8558
kind: List
metadata:
  resourceVersion: ""
  selfLink: ""
```

~~Cluster API の結果を見ると、 oldest から見て unreachable な3つノードのうち、一つしか Down とマークされていない.~~

Cluster API の結果を見ると、 oldest に属さない3つノードは全てクラスターから除去されていることが分かる.
