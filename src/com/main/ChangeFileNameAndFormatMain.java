package com.main;

import com.main.util.FileUtilClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ChangeFileNameAndFormatMain {
    public static void main(String[] args) {

        FileUtilClass fileUtilClass = new FileUtilClass();
        ArrayList<String> fileList =  new ArrayList<>();
        ArrayList<HashMap<String,Object>> fileAttributeList = new ArrayList<HashMap<String,Object>>();
        ArrayList<String> dirList = new ArrayList<>();
        ArrayList<String> targetDirList = new ArrayList<>();
        String tempPath;
        String tempTargetPath;
        //깃허브 공부내용 로컬저장소 위치 - 노트북
        //String path = "C:\\Users\\USER\\Desktop\\kimsc\\0.깃허브-개발공부";
        //깃허브 공부내용 로컬저장소 위치 - 집
        String path ="C:\\Users\\sung\\Desktop\\개발\\study";
        //블로그 업로드 파일 위치
        String targetPath = "C:\\IntelliJProject\\kkimsungchul.github.io\\_posts";
        String blogCreateDirPath;
        String blogDeleteDirPath;

        //fileUtilClass.insertYmlTag(null);



        System.out.println("## 파일 전체 탐색 시작");
        dirList = fileUtilClass.getDirList(path);
        targetDirList = fileUtilClass.getDirList(targetPath);

        System.out.println("## 디렉토리 목록 출력");
        for(String dirName : dirList){
            System.out.println(dirName);
        }

        System.out.println("## 파일 전체 탐색 시작");
        for(String dirName : dirList){
            tempPath = path+ File.separator+dirName;
            //fileList.addAll(fileUtilClass.getFileList(tempPath));
            fileList.addAll(fileUtilClass.getFilePathAndNameList(tempPath));
        }


        System.out.println("## 파일 전체 출력");
        for(String fileName : fileList){
            System.out.println(fileName);
        }

        System.out.println("## 파일 인코딩 변경 ANSI -> UTF-8");
        for(String fileName : fileList){
            fileUtilClass.changeEncoding(fileName);

        }


        System.out.println("## 파일의 모든 정보 가져오기");
        for(String dirName : dirList){
            tempPath = path+File.separator+dirName;
            tempTargetPath = targetPath + "\\"+dirName;
            fileAttributeList.addAll(fileUtilClass.getFileAttributeList(tempPath , tempTargetPath));
        }
        for(HashMap<String,Object> map : fileAttributeList){
            System.out.println(map);
        }

        System.out.println("## 파일의 첫번째 라인의 특수문자 변경");
        for(HashMap<String,Object> map : fileAttributeList){
            fileUtilClass.changeFirstLine(map);
        }


        //모든 파일 정보 fileAttributeList
        //모든 디렉토리 정보 dirList
        System.out.println("## 블로그 폴더 전체 삭제");
        for(String targetDirName : targetDirList){
            blogDeleteDirPath = targetPath+File.separator+targetDirName;
            fileUtilClass.deleteDirectoryAndFile(blogDeleteDirPath);
        }



        System.out.println("## 블로그 폴더에 디렉토리 생성");
        for(String dirName : dirList){
            blogCreateDirPath = targetPath+File.separator+dirName;
            if(!fileUtilClass.makeDirectory(blogCreateDirPath)){
                System.out.println("## 이미 존재하는 폴더 입니다. : " + dirName);
            }
        }

        System.out.println("## 파일 복사");
        for(HashMap<String,Object> map : fileAttributeList){
            String newFilePath =fileUtilClass.copyFile(map);
            map.put("newFilePath" , newFilePath) ;
        }

        System.out.println("## 복사한 파일에 태그 삽입");
        for(HashMap<String,Object> map : fileAttributeList){
            fileUtilClass.insertYmlTag(map);
        }

    }
}