import subprocess, json
command = "kubectl get pods -o json --sort-by=.status.podIP".split(" ")
proc = subprocess.run(command, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
result = json.loads(proc.stdout.decode("utf8"))["items"]
result = [(d["metadata"]["name"], d["status"]["podIP"]) for d in result]
result = [(name, ip) for name, ip in result if name.startswith("akka-cluster-test-")]
# print(result)
head_two = result[:2]
rest_three = result[2:]
for name, ip in head_two:
    for rest_name, rest_ip in rest_three:
        print(f"kubectl exec {name} -- route add -host {rest_ip} reject")
for name, ip in head_two:
    for rest_name, rest_ip in rest_three:
        print(f"kubectl exec {rest_name} -- route add -host {ip} reject")
