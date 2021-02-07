package NSD.Server;

import NSD.Tools.Database;

public class Main {

    public static void main(String[] args) {

        Database db = new Database();
        App app = new App(3000, 3);

    }
}
