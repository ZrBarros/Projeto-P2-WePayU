import easyaccept.EasyAccept;

public class Main {
    public static void main(String[] args) {
        String facade = "br.ufal.ic.p2.wepayu.Facade";
        String[] testes = args.length == 0 ? new String[]{
                "tests/us1.txt", "tests/us1_1.txt",
                "tests/us2.txt", "tests/us2_1.txt",
                "tests/us3.txt", "tests/us3_1.txt",
                "tests/us4.txt", "tests/us4_1.txt",
                "tests/us5.txt", "tests/us5_1.txt",
                "tests/us6.txt", "tests/us6_1.txt",
                "tests/us7.txt", "tests/us8.txt",
                "tests/us9.txt", "tests/us9_1.txt"
        } : args;
        for (String teste : testes) {
            EasyAccept.main(new String[]{facade, teste});
        }
    }
}

