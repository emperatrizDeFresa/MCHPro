package emperatriz.common;

/**
 * Created by ramon on 29/07/2016.
 */
public class WappDto {
    public String name,url;

    public WappDto(){

    }

    public WappDto(String name, String url){
        this.name=name;
        this.url=url;
    }

    @Override
    public boolean equals(Object obj){
        try{
            return this.url.equals(((WappDto)obj).url);
        }catch (Exception ex){
            return false;
        }
    }
}
