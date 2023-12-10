#!/bin/bash

export CLASSPATH=$CLASSPATH:./jgroups-3.6.4.Final.jar

# necessário forçar IPv4 para funcionar na rede cabeada dos laboratórios do IFMG  
IPV4="-Djava.net.preferIPv4Stack=true"

# necessário no L.A.R. caso existam muitas interfaces virtuais de rede com nomes iniciados com br-, docker0, etc
get_IP_of_interface(){
    if [[ -n $1 ]]; then 
        ip addr | grep "$1" -A5 | grep 'inet ' | sed 's/ *inet //; s/\/.*//'
    else 
        ip addr | grep -E "(eno1|eth0|en0)" -A5 | grep 'inet ' | sed 's/ *inet //; s/\/.*//'
        #ip addr | grep 'inet ' | grep '172.22.70.' | sed 's/.*inet //; s/\/27 .*//'
    fi
}
MEU_IP="$(get_IP_of_interface )"
IPV4="$IPV4 -Djgroups.bind_addr=$MEU_IP"
echo "$IPV4" 

if [ -n "$1" ]; then 
	java "$IPV4" org.jgroups.tests.perf.MPerf -props "$1"
else
	java "$IPV4" org.jgroups.tests.perf.MPerf -props "banco.xml"
fi
