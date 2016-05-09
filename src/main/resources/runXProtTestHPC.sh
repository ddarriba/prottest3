jarfile=`find . -name "prottest-3.*.jar" | sort | tail -n 1`
java -cp $jarfile es.uvigo.darwin.xprottest.XProtTestApp
