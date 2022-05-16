
echo "Usage: ./runmyplayer.sh <white|black> (timeout) (ip_address)"
echo "Player colour is mandatory, timeout and ip_address are optional; in this case timeout=60sec and ip_address = localhost"

if [ $# -eq 3 ]; then
	java -jar /home/tablut/tablut/artificialFailure.jar "$1" "$2" "$3"
elif [ $# -eq 2 ]; then
	java -jar /home/tablut/tablut/artificialFailure.jar "$1" "$2" "localhost"
elif [ $# -eq 1 ]; then
	java -jar artificialFailure.jar "$1" "60" "localhost"
else
	echo "Usage: ./runmyplayer.sh <white|black> (timeout) (ip_address)"
fi
