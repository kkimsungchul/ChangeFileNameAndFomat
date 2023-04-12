package com.main.util;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;


//파일유틸
public class FileUtilClass {


    /**
     * 디렉토리 탐색
     * @param path 디렉토리 경로
     * @return dirList 디렉토리 목록
     * */
    public ArrayList<String> getDirList(String path){
        ArrayList<String> dirList = new ArrayList<>();
        //디렉토리 내에 파일 및 디렉토리 탐색
        for (File info : new File(path).listFiles()) {
            //디렉토리 탐색
            if (info.isDirectory() && !info.getName().startsWith(".")) {
                    dirList.add(info.getName());
                }
            }
//            //파일 탐색
//            if (info.isFile()) {
//
//            }
        return dirList;
    }



    /**
     * 파일명 가져오기, txt , markdown 확장자만 가져옴
     * @param directory 디렉토리 경로
     * @return files 파일명 목록
     * */
    public ArrayList<String> getFileList(String directory) {
        ArrayList<String> files = new ArrayList<String>();
        File dir = new File(directory);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".txt")) || file.getName().endsWith((".markdown")) || file.getName().endsWith((".html"))) {
                files.add(file.getName());
            }
        }
        return files;
    }




    /**
     * 절대경로+파일명 가져오기
     * @param directory 디렉토리 경로
     * @return files 파일명 목록
     * */
    public ArrayList<String> getFilePathAndNameList(String directory) {
        ArrayList<String> files = new ArrayList<String>();
        File dir = new File(directory);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".txt"))) {
                files.add(directory+"\\"+file.getName());
            }
        }
        return files;
    }

    /**
     * 파일의 속성값 전부다 가져오기
     * @param directory 디렉토리 경로
     * @return files 파일명 목록
     * */
    public ArrayList<HashMap<String,Object>> getFileAttributeList(String directory , String targetDirPath){
        ArrayList<HashMap<String,Object>> fileAttributeList = new ArrayList<HashMap<String,Object>>();
        File dir = new File(directory);
        Path path;
        String filePath = "";
        String targetPath="";
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".txt"))) {
                filePath = directory+"\\"+file.getName();
                path = Paths.get(filePath);
                try{
                    Map<String,Object> tempMap =  Files.readAttributes(path,"*");
                    //{lastAccessTime=2022-09-14T00:05:08.771079Z, lastModifiedTime=2022-09-14T00:05:08.771079Z, size=2429, creationTime=2022-09-14T00:05:08.770077Z, isSymbolicLink=false, isRegularFile=true, fileKey=null, isOther=false, isDirectory=false}
                    HashMap<String,Object> fileAttributeMap = new HashMap<>();
                    fileAttributeMap.put("fileName",file.getName());
                    fileAttributeMap.put("path",filePath);
                    fileAttributeMap.put("lastModifiedTime",tempMap.get("lastModifiedTime"));
                    fileAttributeMap.put("targetDirPath",targetDirPath);
                    fileAttributeList.add(fileAttributeMap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return fileAttributeList;
    }



    /**
     * 디렉토리 생성
     * @param path 디렉토리 경로
     * */
    public boolean makeDirectory(String path){
        File file = new File(path);
        return file.mkdirs();
    }

    /**
     * 디렉토리 삭제
     * @param path 디렉토리 경로
     */
    public void deleteDirectoryAndFile(String path){
        Path directoryPath = Paths.get(path);
        try{
            Files.deleteIfExists(directoryPath);
        }catch (DirectoryNotEmptyException e){
            //디렉토리 밑에 파일이 있을경우 java.nio.file.DirectoryNotEmptyException 발생
            deleteSubFile(path);
            deleteDirectoryAndFile(path);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("## 시스템 에러");
        }
    }

    /**
     * 하위 디렉토리내 파일 삭제
     * @param path 디렉토리 경로
     * */
    public void deleteSubFile(String path){
        ArrayList<String> fileList = getFileList(path);
        for(String fileName :fileList){
            deleteDirectoryAndFile(path+"\\"+fileName);
        }

    }

    /**
     * 파일 복사
     * @param map 파일에 대한 정보
     * */
    public String copyFile(HashMap<String,Object> map){
        String newFileName = changeFileName(map.get("fileName").toString() ,map.get("lastModifiedTime").toString());
        String newFilePath = map.get("targetDirPath").toString()+"\\"+newFileName;
        File file = new File(map.get("path").toString());
        File newFile = new File(newFilePath);
        try{
            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            e.printStackTrace();
        }
        return newFilePath;
    }


    /**
     * 파일명 변경, 블로그에 맞는 템플릿 파일명으로 변경
     * @param fileName 파일명
     * @param date 날짜
     * */
    public String changeFileName(String fileName , String date){
        //date = date.substring(0,10);
        //파일명 앞에 들어가는 날자를 모두 0000-00-00으로 고정
        date = "0000-00-00";
        fileName = fileName.replaceAll("\\[" ,"");
        fileName = fileName.replaceAll("]" ,"");
        fileName = fileName.trim();
        fileName = fileName.replaceAll(" " , "-");
        fileName = fileName.replaceAll(".txt" , ".markdown");
        return date+"-"+fileName;
    }

    /**
     * 파일 인코딩 변경
     * @param filePath 파일경로
     * */
    public void changeEncoding(String filePath){
        File file = new File(filePath);
        String encoding="";
        try{
            encoding  = readEncoding(file);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(encoding != null && !encoding.equals("UTF-8")) {
            try{
                Path path = Paths.get(filePath);
                ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(path));
                CharBuffer charBuffer = Charset.forName("CP949").decode(byteBuffer);
                byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
                Files.write(path, byteBuffer.array());
            }catch (Exception e){
                System.out.println("## 인코딩 변경 실패 , " +filePath);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 파일 인코딩 타입 확인
     * @param file 타입을 확인할 파일
     * */

    public String readEncoding(File file) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        buf = null;
        fis.close();
        return encoding == null?"UTF-8":encoding;
    }


//{
// path=C:\Users\USER\Desktop\kimsc\0.깃허브-개발공부\Vue\[Vue] 파일 업로드.txt,
// fileName=[Vue] 파일 업로드.txt, 
// lastModifiedTime=2023-03-24T10:38:38.66808Z, 
// targetDirPath=C:\IntellijProject\kkimsungchul.github.io\_posts\Vue, 
// newFilePath=C:\IntellijProject\kkimsungchul.github.io\_posts\Vue\2023-03-24-Vue-파일-업로드.markdown
// }


    /**
     * Github에 업로드할 파일에 yaml 태그 입력
     * @param fileMap yaml 태그를 생성할 파일의 정보
     * */
    public void insertYmlTag(HashMap<String,Object> fileMap){
        HashMap<String,String> createYmlTagMap = createYmlTag(fileMap);
        int count =0;
        try{
            List<String> lines = Files.readAllLines(Paths.get(createYmlTagMap.get("filePath")));
            //파일상단, yaml 태그 밑에 특수문자 사용을 위해  {% endraw %} 추가
            lines.add(0,"{% raw %}");
            lines.add(0,"---");
            lines.add(0,createYmlTagMap.get("categories"));
            lines.add(0,createYmlTagMap.get("date"));
            lines.add(0,createYmlTagMap.get("subtitle"));
            lines.add(0,createYmlTagMap.get("title"));
            lines.add(0,"layout: post");
            lines.add(0,"---");

            File file = new File(createYmlTagMap.get("filePath"));
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // 4. 파일에 쓰기
            int lineLength =1;
            for(String line : lines){
                //사용하지 않는 우측 공백 제거
                line = line.replaceAll("\\s+$","");
                
                //#은제목, ##은 소제목으로 사용하기위해 변환, 다만 구분자로####이렇게 사용한적이 있어서 첫번째 #만 ##으로 변경
                line = line.replaceFirst("#","##");

//                //공백 2개 이상 제거
//                line = line.replaceAll("\\s+"," ");
                //공백을 넣는 이유는 markdown 에서 공백 두칸 후 엔터를입력해야 줄바꿈으로 인식함
                //엔터 여러줄 입력 방지
                if(lineLength==0){
                    if(line.length()!=0){
                        fileWriter.write(line+"  ");
                        fileWriter.write("\n");
                        lineLength = line.length();
                    }else{
                        lineLength = 0;
                    }
                }else{
                    fileWriter.write(line+"  ");
                    fileWriter.write("\n");
                    lineLength = line.length();
                }
            }
            //파일 제일 하단에 {% endraw %} 추가
            fileWriter.write("{% endraw %}");
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("## file tag insert success : " +createYmlTagMap.get("title"));
    }

    /**
     * Github에 업로드할 파일의 Yaml 태그 생성
     * @param fileMap yaml 태그를 생성할 파일의 정보
     * */
    public HashMap<String,String> createYmlTag(HashMap<String,Object> fileMap){
        HashMap<String,String> createYmlTagMap = new HashMap<>();

        String filePath =fileMap.get("newFilePath").toString();
        String title ="title: ";
        String date = "date: 0000-00-00 00:00:00 +0900";
        String subtitle="subtitle: ";
        String categories="categories: ";
        String layout="layout: post";

        title = title + "\"" +fileMap.get("fileName").toString().replaceAll(".txt","")+"\"";


//        date = date + fileMap.get("lastModifiedTime").toString().replaceAll("T"," ");
//        date = date.substring(0,25) + " +0900";
//        2019-03-26T08:06:08Z


        //targetDirPath=C:\IntellijProject\kkimsungchul.github.io\_posts\Vue,
        String[] temp = fileMap.get("targetDirPath").toString().split("\\\\");
        categories = categories + temp[temp.length-1].replaceAll(" ","");
        //categories = categories + fileMap.get("fileName").toString().substring(fileMap.get("fileName").toString().indexOf("\\[")+2,fileMap.get("fileName").toString().indexOf("]")).replaceAll(" ","");


        String tempSubtitle = fileMap.get("fileName").toString();
        subtitle = subtitle + title.replaceAll("\\[","").replaceAll("]","").replaceAll("title:","").trim();

        createYmlTagMap.put("filePath",filePath);
        createYmlTagMap.put("title" , title);
        createYmlTagMap.put("date" , date);
        createYmlTagMap.put("subtitle" , subtitle);
        createYmlTagMap.put("categories" , categories);
        createYmlTagMap.put("layout" , layout);

        return createYmlTagMap;

    }

    /**
     * study 폴더에 있는 텍스트 파일의 최상단 [ ,] 특수문자 제거
     * @param map 첫라인을 변경할 파일 정보
     * */
    public void changeFirstLine(HashMap<String,Object> map){
        String line="";
        try{
            //순서 중요, 해당파일의 내용들을 먼저 리스트에 담고나서 작업해야함
            //File 객체를 먼저 만들어버릴 경우 해당 파일의 내용이 전부다 지워짐
            List<String> lines = Files.readAllLines(Paths.get(map.get("path").toString()));

            //시작문자에 []가 포함되어 있지 않으면 수정하지 않음
            if(!line.contains("[") && !line.contains("]")){
                return;
            }

            File file = new File(map.get("path").toString());
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


            for(int i=0;i<lines.size();i++){
                line = lines.get(i);
                
                //첫번째 라인의 특수문자 치환
                if(i==0){
                    line = line.trim();
                    if(line.contains("[") && line.contains("]")){
                        line = line.replaceAll("\\[" , "");
                        line = line.replaceAll("]" , "");
                        line = line.trim();
                        line = "# " + line;
                    }
                }
                fileWriter.write(line);
                fileWriter.write("\n");

            }
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("## success change first line : " + map.get("fileName"));

    }


    public void getFirstLine(HashMap<String,Object> map){
        try{
            List<String> lines = Files.readAllLines(Paths.get(map.get("path").toString()));
            if(!lines.get(0).startsWith("#")){
                System.out.println(lines.get(0));
                System.out.println(map.get("path").toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}



