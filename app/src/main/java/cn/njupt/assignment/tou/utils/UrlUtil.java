package cn.njupt.assignment.tou.utils;

import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: sherman
 * @date: 2021/10/12
 * @description: 工具类：处理url拿到icon；判断url是否合法
 */
public class UrlUtil {

    public static String modifyPrefix(String str){
        StringBuilder url = new StringBuilder();
        String[] s;
        if (str.contains("://")){
            try{
                s = str.split("://");
            }catch (Exception e){
                return null;
            }
            if (s.length!=2)
                return null;
            if (s[1].startsWith("www."))
                url.append(s[0]).append("://").append(s[1]);
            else
                url.append(s[0]).append("://").append("www.").append(s[1]);
            return url.toString();
        }else{
            if (str.startsWith("www."))
                url.append("https://").append(str);
            else
                url.append("https://"+"www."+str);
            return url.toString();
        }
    }

    public static boolean isTopURL(String str){
        str = str.toLowerCase();
        String domainRules = "wiki|edu.cn|com.cn|net.cn|org.cn|gov.cn|com.hk|公司|中国|网络|com|net|org|int|edu|gov|mil|arpa|Asia|biz|info|name|pro|coop|aero|museum|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cf|cg|ch|ci|ck|cl|cm|cn|co|cq|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|es|et|ev|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gr|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|in|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|ml|mm|mn|mo|mp|mq|mr|ms|mt|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|va|vc|ve|vg|vn|vu|wf|ws|ye|yu|za|zm|zr|zw";
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "((m+\\.)?)"
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "("+domainRules+"))"
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|"
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isUrl = pattern.matcher(str);
        return isUrl.matches();
    }

    public static String getIconUrl(String url){
        StringBuilder iconUrl;
        StringBuilder suf = new StringBuilder();
        if (url.startsWith("https://")||url.startsWith("http://")){

        }
        String[] s = url.split("://");
        String[] s1 = Uri.parse(url).getHost().split("\\.");
        if(s1[0].equals("m")){
//            iconUrl = new StringBuilder(s[0] + "://www.");
            for(int i=1;i<s1.length;i++){
                if (i!=s1.length-1)
                    suf.append(s1[i]).append(".");
                else
                    suf.append(s1[i]);
            }
            if(isTopURL(new StringBuilder(s[0]).append(suf).append("/favicon.ico").toString())){
                iconUrl = new StringBuilder(s[0]).append("://").append(suf).append("/favicon.ico");
                System.out.println("11"+iconUrl);
            }else{
                iconUrl = new StringBuilder(s[0]).append("://www.").append(suf).append("/favicon.ico");
                System.out.println("22"+iconUrl);
            }
//            iconUrl.append("/favicon.ico");
        }else{
            iconUrl = new StringBuilder(s[0] + "://" + Uri.parse(url).getHost() + "/favicon.ico");
        }
        return iconUrl.toString();
    }

}
