package ru.fastcards.social.api;

@SuppressWarnings("serial")
public class KException extends Exception{
    KException(int code, String message, String url){
        super(message);
        error_code=code;
        this.url=url;
    }
    /**
	 * @uml.property  name="error_code"
	 */
    public int error_code;
    /**
	 * @uml.property  name="url"
	 */
    public String url;
    
    //for captcha
    /**
	 * @uml.property  name="captcha_img"
	 */
    public String captcha_img;
    /**
	 * @uml.property  name="captcha_sid"
	 */
    public String captcha_sid;
}
