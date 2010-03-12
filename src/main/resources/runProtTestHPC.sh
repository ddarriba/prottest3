export MPJ_HOME=$PWD/mpj
export NP=$1
shift
$MPJ_HOME/bin/mpjrun.sh -wdir $PWD/ -np $NP -jar prottest-hpc.jar  $* 
