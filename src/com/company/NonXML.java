package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.IOUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Nisansa on 15/11/15.
 */
public class NonXML {

    String docLine="";
    ArrayList<String> pubMedIds=new ArrayList<String>();
    ArrayList<String> errorPubMedIds=new ArrayList<String>();

    public static void main(String[] args) {
        // write your code here
        NonXML nx=new NonXML();

    }

    public NonXML() {
        readList();
        for(int i=0;i<pubMedIds.size();i++) {
            String pubMedid=pubMedIds.get(i);
            System.out.println((i+1)+" out of "+pubMedIds.size()+ " is "+  pubMedid);
            createXML(pubMedid);
        }
    }

    private void readList(){
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("pubmed-list.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line=null;
            while((line = bufferedReader.readLine()) != null) {
              //  System.out.println(line);
                pubMedIds.add(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createXML(String pubMedid){
        try {
            String inputLine;

            //http://www.ncbi.nlm.nih.gov/pubmed/26173778?report=docsum&format=text
            URL u = new URL("http://www.ncbi.nlm.nih.gov/pubmed/"+pubMedid+"?report=docsum&format=text");
            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
            String summary="";
            boolean collect=false;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("<pre>")) {
                    collect=true;
                }
                else if (inputLine.contains("</pre>")) {
                    break;
                }
                else{
                    if(collect){
                        summary=summary+inputLine;
                    }
                }
            }
            summary="\n<Summary>"+summary+"</Summary>";
            //System.out.println(summary);

            u = new URL("http://www.ncbi.nlm.nih.gov/pubmed/?term="+pubMedid+"&report=xml&format=text");
            in = new BufferedReader(new InputStreamReader(u.openStream()));
            docLine="";
            inputLine="";
            collect=false;
            boolean thisTitleCaught=false;
            boolean writeOut=true; //Made it true for all

            while ((inputLine = in.readLine()) != null) {

                if(!thisTitleCaught && inputLine.contains("PMID")){
                    docLine=docLine+inputLine;
                    thisTitleCaught=true;
                    docLine=docLine+summary;
                }



                if(inputLine.contains("MeshHeadingList")) {
                    if(collect){
                        break;
                    }
                    else{
                        collect=true;
                        writeOut=true;
                    }
                }


                if(collect){
                    docLine=docLine+inputLine;
                }
            }
            docLine=docLine.replace("&lt;","<");
            docLine=docLine.replace("&gt;",">");
            docLine=docLine.replace("        ","\n");
            docLine=docLine.replace("\n\n\n","\t");
            docLine=docLine.replace("\n\n","\t");
            docLine=docLine.replace("\t","\n");
            docLine=docLine.replace("\n<","\n\t\t<");
            docLine=docLine.replace("\t\t<PMID","<PMID");
            docLine=docLine.replace("\t\t<MeshHeading","<MeshHeading");
            docLine=docLine.replace("\t\t<Summary","<Summary");
            if(collect) {
                docLine = docLine + "\n</MeshHeadingList>";
            }
            docLine=docLine.replace("\n","\n\t");
            docLine=docLine.replace("<Summary>","<Summary>\n\t\t");
            docLine=docLine.replace("</Summary>","\n\t</Summary>");
            docLine="<root>"+docLine+"\n</root>";

           // System.out.println(docLine);

            if(writeOut){
                writeToFile(pubMedid,docLine);
            }

            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String id,String content){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./output/"+id+".xml", "UTF-8");
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
