package ru.fastcards.common;

import ru.fastcards.utils.Constants;
import android.graphics.Bitmap;

public class Postcard{
	private int id;
	private String name;
//	private Bitmap thumbBitmap;
	private Bitmap largeBitmap;
	private boolean isSelected;
	private String text;
//	private Appeal appeal; 
//	private String signature; 
	private String signatureBitmapUri;
//	private int state_appeal;
//	private int state_signature;

//	private int recourseId;
//	private int imageResourse;
	
	private int form;
	
	private Bitmap verticalSignature;
	private Bitmap printSignature;
	
	public String getText(){
		return text;
	}
	
	
	public void setText(String text){
		this.text=text;
	}
	
//	public Appeal getAppeal(){
//		return appeal;
//	}
//	
//	public void setAppeal(Appeal appeal){
//		this.appeal=appeal;
//	}
//	
//	public String getSignature(){
//		return signature;
//	}
//	
//	public void setSignature(String signature){
//		this.signature=signature;
//	}
	
	public Bitmap getSignatureImage(){
		switch (form){
		case Constants.POSTCARD_VERTICAL:
			return verticalSignature;
		case Constants.POSTCARD_HORIZONTAL:
			return printSignature;
		}
		return null;
	}

	
	public String getSignatureBitmapUri(){
		return signatureBitmapUri;
	}
	
	public void setSignatureBitmapUri(String uri){
		this.signatureBitmapUri=uri;
	}
	
//	public void setStateAppeal(int state_title){
//		this.state_appeal=state_title;
//	}
//	public int getStateAppeal(){
//		return state_appeal;
//	}
//	public void setStateSignature(int state_signature){
//		this.state_signature=state_signature;
//	}
//	public int getStateSignature(){
//		return state_signature;
//	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

//	public Bitmap getThumbBitmap() {
//		return thumbBitmap;
//	}
//	public void setThumbBitmap(Bitmap thumbBitmap) {
//		this.thumbBitmap = thumbBitmap;
//	}
	
	public Bitmap getLargeBitmap() {
		return largeBitmap;
	}
	public void setLargeBitmap(Bitmap largeBitmap) {
		this.largeBitmap = largeBitmap;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
//	public String getThumb() {
//		return name;
//	}

//	public void setImageView(ImageView itemImageView) {	
//		imageView = itemImageView;
//	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		
	}
//	public Bitmap getImageBitmapThumb() {
//		// TODO Auto-generated method stub
//		return thumbBitmap;
//	}

//	public int getResourseId() {
//		return recourseId;
//	}
//
//	public void setResourseId(int recourceId) {
//		this.recourseId = recourceId;
//	}

//	public int getImageResource() {
//		return imageResourse;
//	}
//
//	public void setImageResource(int imageResource) {
//		this.imageResourse = imageResource;
//	}
	
	public int getForm(){
		return form;
	}
	
	public void setForm(int form){
		this.form=form;
	}
	
	public void setVerticalSignature(Bitmap bitmap){
		this.verticalSignature=bitmap;
	}
	
	public void setPrintSignature(Bitmap bitmap){
		this.printSignature=bitmap;
	}

}
