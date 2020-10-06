package com.example.dell.demo.MeRelative;

/*该类是个人中心界面的选项，其对象作为数据传给RecyclerView*/

public class MeOption {

    private String txtOption;
    private int imgOption;

    public String getTxtOption() {
        return txtOption;
    }

    public void setTxtOption(String txtOption) {
        this.txtOption = txtOption;
    }

    public int getImgOption() {
        return imgOption;
    }

    public void setImgOption(int imgOption) {
        this.imgOption = imgOption;
    }

    public MeOption(String metext, int meimage){
        this.txtOption=metext;
        this.imgOption=meimage;
    }

    public MeOption(String meText){
        txtOption=meText;
    }

}
