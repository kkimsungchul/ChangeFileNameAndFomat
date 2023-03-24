package com.main.util;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.SimpleDateFormat;
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
        date = date.substring(0,10);
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


    public void insertYmlTag(HashMap<String,Object> fileMap){
        HashMap<String,String> createYmlTagMap = createYmlTag(fileMap);
        try{
            List<String> lines = Files.readAllLines(Paths.get(createYmlTagMap.get("filePath")));
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

            for(String line : lines){
//                System.out.println(line);
                fileWriter.write(line);
                fileWriter.write("\n");
            }

            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("## file tag insert success : " +createYmlTagMap.get("title"));
    }

    public HashMap<String,String> createYmlTag(HashMap<String,Object> fileMap){
        HashMap<String,String> createYmlTagMap = new HashMap<>();

        String filePath =fileMap.get("newFilePath").toString();
        String title ="title: ";
        String date="date: ";
        String subtitle="subtitle: ";
        String categories="categories: ";
        String layout="layout: post";

        title = title + "\"" +fileMap.get("fileName").toString().replaceAll(".txt","")+"\"";


        date = date + fileMap.get("lastModifiedTime").toString().replaceAll("T"," ");
        date = date.substring(0,25) + " +0900";
        //2019-03-26T08:06:08Z
        categories = categories + fileMap.get("fileName").toString().substring(fileMap.get("fileName").toString().indexOf("\\[")+2,fileMap.get("fileName").toString().indexOf("]")).replaceAll(" ","");
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



    
    



    //////
    
    /*
    * fileList : 로그 파일 목록
    * filePath : 로그 파일 경로
    * filePath : 탐지할 단어 목록
    * filePath : 작업명 (IIS , APACHE 등)
    * saveLogDir : 분석 완료한 로그 저장 경로
    * props : 설정파일
    * fis : 파일 스트림
    * moveCompleteLog : 파일 이동 여부
    * */
    //파일목록에서 공격로그 탐지
    public ArrayList<String> attackDetection(List<String> fileList , String filePath , String[] blackList,String workName , String saveLogDir , Properties props,FileInputStream fis,String moveCompleteLog)
            throws Exception{

        ArrayList<String> returnList = new ArrayList();
        String aaa;
        String custString="";
        String originalReadLine="";
        String exceptString="";
        int startSearchIndex=0;
        int addIndex=0;
        int lineNumber=0;

//        if(filePath.contains(workName)){

        //로그의 라인에서  검색 시작 위치 지정
        props.load(new BufferedInputStream(fis));
        exceptString = new String(props.getProperty(workName+"ExceptString").getBytes("ISO-8859-1"), "utf-8");
        System.out.println("#### 검색 시작 위치 : " + exceptString);
//        }


        for(int i=0;i<fileList.size();i++){
            try{

                //넘어온 경로가 파일인지 폴더인지 체크,
                //폴더일 경우 해당 디렉토리에서 읽을 라인이 없음
                File f = new File(filePath+fileList.get(i));
                lineNumber=0;
                if(f.isFile()){

                    FileReader rw = new FileReader(filePath+fileList.get(i));
                    BufferedReader br = new BufferedReader( rw );
                    //읽을 라인이 없을 경우 br은 null을 리턴한다.
                    String readLine = null ;
                    //로그파일 읽기
                    while( ( readLine =  br.readLine()) != null ){


                        lineNumber++;
                        startSearchIndex = readLine.indexOf(exceptString);
                        if(startSearchIndex<0){
                            startSearchIndex = 0;
                        }else{
                            startSearchIndex = addIndex;
                        }
                        originalReadLine = readLine;
                        readLine= readLine.substring(startSearchIndex);
                        returnList = searchBlackList(returnList , blackList , fileList.get(i) , readLine , originalReadLine,lineNumber);
                    }
                    rw.close();
                    br.close();
                }
            }catch ( IOException e ) {
                System.out.println(e);
            }

            moveFile(filePath+fileList.get(i),saveLogDir + "\\"+fileList.get(i),moveCompleteLog);
        }





        return returnList;
    }



    //블랙리스트 문자열 탐색
    /*
     * returnList : 탐지된 목록 리스트
     * blackList : 탐지할 단어 배열
     * fileName : 현재 탐지중인 파일 이름
     * readLine : 현재 탐지하는 라인
     * originalReadLine : 현재 탐지하는 라인의 원본
     * targetLine : 파라메터 부분
     * subStringStartIndex : 자를 라인의 위치
     * */
    public ArrayList<String> searchBlackList(ArrayList<String > returnList , String[] blackList, String fileName , String readLine , String originalReadLine,int lineNumber){

        int overlapLineCheck =0;
        String targetLine="";
        int getByte=originalReadLine.getBytes().length;

        int subStringStartIndex = 0;
        if(getByte>=1024){
            returnList.add("검출된 파일 : " + fileName + "\r\n");
            returnList.add("검출된 단어 : 해당 문자열의 길이가 1024바이트를 초과하였습니다. \r\n");
            returnList.add("검출된 라인 : " + originalReadLine + "\r\n");
            returnList.add("검출된 라인 번호 : " + lineNumber + "\r\n");
            returnList.add("------------------------------------" + "\r\n");
            //db insert문으로 변경
        }

        //블랙리스트 문자열 검색
        for(int j=0; j<blackList.length;j++){
            //중복라인 검출을 막기위해 추가
            if(overlapLineCheck==1){
                break;
            }
            //파라메터가 있는 경우에만 검사
            subStringStartIndex = readLine.indexOf("?");
            if(subStringStartIndex<0){
                break;
            }


            targetLine = readLine.substring(subStringStartIndex);

            if(targetLine.contains(blackList[j])){
                returnList.add("검출된 파일 : " + fileName + "\r\n");
                returnList.add("검출된 단어 : " + blackList[j] + "\r\n");
                returnList.add("검출된 라인 : " + originalReadLine + "\r\n");
                returnList.add("검출된 라인 번호 : " + lineNumber + "\r\n");
                returnList.add("------------------------------------" + "\r\n");
                overlapLineCheck=1;
            }
        }
        return returnList;
    }



    //탐지된 목록 저장
    /*
    * outList : 탐지된 목록이 저장된 리스트
    * filePath : 파일 저장 경로
    * */
    public void saveFile(ArrayList<String> outList , String filePath){
        String saveFileName;
        String saveDirName;
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyyMM");
        Date time = new Date();
        saveFileName = format1.format(time);
        saveDirName = format2.format(time);
        //현재는 날짜로만 디렉토리 이름을 구분하고 있지만, 한군대다가 모으게된다면 아래의 내용을 IF문으로 분기
        String saveFilePath = filePath  + saveDirName;

        //디렉토리 생성, 있을경우 아무작업도 안함
        //makeDirectory(saveFilePath);

         for(int a=0;a<outList.size();a++){

            //저장할 라인 한줄씩 출력
            //System.out.println(outList.get(a));
            String message = outList.get(a);

            File file = new File(saveFilePath + "\\" +  saveFileName + "_analysis_log.txt");
            FileWriter writer = null;

            try {
                // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
                writer = new FileWriter(file, true);
                writer.write(message);
                writer.flush();


            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(writer != null) writer.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }





    //분석한 로그파일 이동
    /*
    * readFilePath : 읽은 파일 경로
    * moveDirPath : 이동할 파일 경로와 파일명
    * moveCompleteLog : 파일 이동 여부 ( YES , NO 옵션값 )
    *
    * */
    public void moveFile(String readFilePath , String moveDirPath , String moveCompleteLog ) {

        //파일을 이동할지 여부 확인
        if(moveCompleteLog.equalsIgnoreCase("NO")){
            return;
        }
        //넘어온 경로가 파일인지 폴더인지 체크,
        //파일이 없을경우 디렉토리의경로를 가지고 들어오기때문에 체크해줘야함
        File f = new File(readFilePath);
        if(!f.isFile()){
            return ;
        }
        //이미 파일이 있을 경우를 체크하여 존재할 경우 _copy 를 붙임
        while(true){
            f = new File(moveDirPath);
            if(f.exists()){
                moveDirPath = moveDirPath+"_copy";
            }else{
                break;
            }
        }

        System.out.println("### 파일 이동 완료 "  + readFilePath + " -> " + moveDirPath);

        try {
            Path filePath = Paths.get(readFilePath);

            Path filePathToMove = Paths.get(moveDirPath);

            Files.move(filePath, filePathToMove);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



