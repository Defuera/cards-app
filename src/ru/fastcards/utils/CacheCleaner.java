//package ru.fastcards.utils;
//
//import java.io.File;
//
//import android.content.Context;
//import android.util.Log;
//
//public class CacheCleaner {
//	
//	private static final long CACHE_MAX_SIZE=10485760l; //10 Mb
//	private static File cacheDIR;
//	public static CacheCleaner cacheCleaner;
//	private static String TAG="CACHE";
//	
//	public static CacheCleaner getInstance(Context context){
//		if (cacheCleaner==null){
//			cacheCleaner=new CacheCleaner();
//			cacheDIR=new File(context.getFilesDir().getPath());			
//		}
//		return cacheCleaner;
//	}
//	
//	private static long getTotalSpace(){
//		long size = 0;
//		
//        File[] files = cacheDIR.listFiles();
//
//        for (File file : files) {
//            if (file.isFile()) {
//                size += file.length();
//            }
//        }
//		return size;
//	}
//	
//	private static boolean cacheIsOverflow(){
//		return getTotalSpace()>CACHE_MAX_SIZE;
//	}
//	
//	public void clearCache(){
//		Log.d(TAG, "clear cache");
//		File[] files = cacheDIR.listFiles();
//		
//		for (File file : files) {
//			if (!cacheIsOverflow()) break;
//                if (!isCover(file)) file.delete();
//        }
//		Log.e(TAG, "Cashe is cleaned");
//	}
//	
//	private static boolean isCover(File file){
//		String filename=file.getName();
//		if (filename.contains("264x264")) return true;
//		if (file.length()<102400/*100 КБайт*/)return true;
//		return false;
//	}
//	
//	public static void afterClearing(){
//        File[] files = cacheDIR.listFiles();
//		for (File file : files) {
//            if (file.isFile()) {
//                Log.e(TAG, "name="+file.getName()+" "+file.length()+"byte");
//            }
//        }
//	}
//	
//}
