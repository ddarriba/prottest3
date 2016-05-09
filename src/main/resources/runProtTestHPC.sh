export MPJ_HOME=$PWD/mpj
export NP=$1
shift
jarfile=`find . -name "prottest-3.*.jar" | sort | tail -n 1`
$MPJ_HOME/bin/mpjrun.sh -dev niodev -wdir $PWD/ -np $NP -jar $jarfile  $*
