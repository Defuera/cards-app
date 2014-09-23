package ru.fastcards.social.api;

import android.widget.ImageView;

/**
 * 
 * @author User
 *
 */
public interface SocialObject {

abstract String getName();
abstract String getThumbUrl();
abstract String getAdditionalInfo();
abstract long getId();
abstract void setImageView(ImageView itemImageView);
abstract boolean isSelected();
abstract void setSelected(boolean isSelected);
abstract int getGender();

	
}
