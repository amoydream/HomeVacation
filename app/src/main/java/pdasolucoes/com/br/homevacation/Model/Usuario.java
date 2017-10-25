package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 25/10/2017.
 */

public class Usuario{

    private int id;
    private String nome;
    private String login;
    private String senha;
    private int idConta;
    private String errorAutenticao;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getErrorAutenticao() {
        return errorAutenticao;
    }

    public void setErrorAutenticao(String errorAutenticao) {
        this.errorAutenticao = errorAutenticao;
    }
}
