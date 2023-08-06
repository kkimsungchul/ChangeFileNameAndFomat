package com.main;

import com.main.util.FileUtilClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
* study 폴더에 정리한 텍스트 파일에서 "[ 카테고리 - 제목] " 으로 시작하는 부분을 "# 카테고리 - 제목" 으로 변경
* */
public class StudyFolderFileChange {
    public static void main(String[]args){
        FileUtilClass fileUtilClass = new FileUtilClass();
        ArrayList<String> dirList = new ArrayList<>();
        ArrayList<String> targetDirList = new ArrayList<>();
        ArrayList<String> fileList =  new ArrayList<>();
        ArrayList<HashMap<String,Object>> fileAttributeList = new ArrayList<HashMap<String,Object>>();
        String path ="C:\\Users\\sung\\Desktop\\개발\\study";
        String tempPath;
        String tempTargetPath;


        System.out.println("## 파일 전체 탐색 시작");
        dirList = fileUtilClass.getDirList(path);


        System.out.println("## 파일 전체 탐색 시작");
        for(String dirName : dirList){
            tempPath = path+File.separator+dirName;
            //fileList.addAll(fileUtilClass.getFileList(tempPath));
            fileList.addAll(fileUtilClass.getFilePathAndNameList(tempPath));
        }
        System.out.println("## 파일의 모든 정보 가져오기");
        for(String dirName : dirList){
            tempPath = path+ File.separator+dirName;
            tempTargetPath = dirName;
            fileAttributeList.addAll(fileUtilClass.getFileAttributeList(tempPath , tempTargetPath));
        }
        System.out.println("## 파일의 첫번째 라인의 특수문자 변경");
        for(HashMap<String,Object> map : fileAttributeList){
            fileUtilClass.changeFirstLine(map);
        }

        System.out.println("## 파일의 첫번째 라인 확인");
        for(HashMap<String,Object> map : fileAttributeList){
            fileUtilClass.getFirstLine(map);
        }


    }
}
