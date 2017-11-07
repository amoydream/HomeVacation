package pdasolucoes.com.br.homevacation.Model;

import android.graphics.Bitmap;

/**
 * Created by PDA on 02/11/2017.
 */

public class Agenda {

    private int idCheckList;
    private int idCasa;
    private String descricaoCasa;
    private String dataAgenda;
    private String comunidade;
    private String imagem;
    private Bitmap imagemBitMap;

    public Bitmap getImagemBitMap() {
        return imagemBitMap;
    }

    public void setImagemBitMap(Bitmap imagemBitMap) {
        this.imagemBitMap = imagemBitMap;
    }

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }

    public int getIdCheckList() {
        return idCheckList;
    }

    public void setIdCheckList(int idCheckList) {
        this.idCheckList = idCheckList;
    }

    public String getDescricaoCasa() {
        return descricaoCasa;
    }

    public void setDescricaoCasa(String descricaoCasa) {
        this.descricaoCasa = descricaoCasa;
    }

    public String getDataAgenda() {
        return dataAgenda;
    }

    public void setDataAgenda(String dataAgenda) {
        this.dataAgenda = dataAgenda;
    }

    public String getComunidade() {
        return comunidade;
    }

    public void setComunidade(String comunidade) {
        this.comunidade = comunidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
