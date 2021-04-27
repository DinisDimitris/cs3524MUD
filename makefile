cs3524mud:
	javac mud/Edge.java; \
	javac mud/MUD.java; \
	javac mud/MUDClient.java; \
	javac mud/MUDClientImpl.java; \
	javac mud/MUDClientInterface.java; \
	javac mud/MUDServerImpl.java; \
	javac mud/MUDServerInterface.java; \
	javac mud/MUDServerMainline.java; \
	javac mud/User.java; \
	javac mud/Vertex.java;

cs3524mudclean:
	rm -f mud/*.class;
