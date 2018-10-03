/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonArray;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataOutputStream;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 *
 * @author Avinash
 */
public class svtRestUtil {

    // Constant Variables
    private static svtFileUtil objFileUtil = svtFileUtil.getInstance();
    
    private String IP;
    private String username;
    private String password;
    
    // Variables inside the class only
    private String baseURL;
    private String token;
    private static svtRestUtil restUtilInst = null;
    
    private svtRestUtil() {   
    }
    
    public static svtRestUtil getInstance() {
    
        if ( null == restUtilInst ) {
            // we need to create the instance and
            // Fill up necessary values and make the 
            // datastructure ready for the user to consume
            restUtilInst = new svtRestUtil();
            /*
            try {
                // This would be called only once
                readConfigFileContent();
            } catch (java.io.IOException eIOException) {
                // We need to sync it in the log file
                System.out.println("CfgFile Exception: " + 
                                    eIOException.toString());
            } 
            */
        }
        if (objFileUtil.isOvcInfoPresent()) {
            restUtilInst.baseURL = "https://" + objFileUtil.getOvcIPAddress() + "/api";
            restUtilInst.IP = objFileUtil.getOvcIPAddress();
            restUtilInst.username = objFileUtil.getOvcUserId();
            restUtilInst.password = objFileUtil.getOvcPassword();
        }
        return restUtilInst;
    }
    // Function to initialize SSL
    private void enableSSL()
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
        }
    }
 
    // Simplivity REST API Token
    public String getAccessToken()
            throws Exception
    {
        enableSSL();

        try {

            if (token != null) {
                return token;
            }
            
            String authUrl = baseURL + "/oauth/token?grant_type=password";
            URL url = new URL(authUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic c2ltcGxpdml0eTo=");

            String urlParameters  = "username=" + username + "&password=" + password;
            byte[] postData       = urlParameters.getBytes( Charset.forName("UTF-8") );
            int    postDataLength = postData.length;

            
            /*
            # DEBUG
            System.out.println("TOKEN BEGIN with username " + authUrl + " : " + urlParameters);
            System.out.println("TOKEN BEGIN with IP " + IP);
            */
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );

            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String message = "Failed to retrieve token : "+ conn.getResponseMessage();
                objFileUtil.logMessage(1, message);
                throw new Exception(message);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
            
            token = jsonReader.readObject().getString("access_token");
            // System.out.println(token);

            return token;

        } catch (Exception e) {
             e.printStackTrace();
             throw new Exception(e.toString());
        }
    }  

    private JsonReader getJsonReader(String uriFilter)
            throws Exception
    {
        enableSSL();

        try {

            if (token == null) {
                getAccessToken();
            }
            
            String searchUrl = baseURL + "/" + uriFilter;
            URL url = new URL(searchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty( "Accept", "application/json");
            
            if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                                    + conn.getResponseCode());
            }

            BufferedReader out = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuffer response = new StringBuffer();
            while ((output = out.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            return Json.createReader(new StringReader(response.toString()));
            // JsonArray arrayList = jsonReader.readObject().getJsonArray(identifier);
        } catch (Exception e) {
             e.printStackTrace();
             throw new Exception(e.toString());
        }
    }

    public HashMap<String, String[]> getClusterHosts()
            throws Exception
    {        
        HashMap<String, String[]> hostClusterMap = new HashMap<String, String[]>();
        try
        {
            JsonReader jsonReader = getJsonReader("hosts");
            JsonArray arrayList = jsonReader.readObject().getJsonArray("hosts");

            HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
            for (int i=0; i<arrayList.size(); i++) {
                JsonObject object = arrayList.getJsonObject(i);
                String host = object.getString("name");
                String cluster = object.getString("compute_cluster_name");
                
                ArrayList<String> tempArray;
                if (map.containsKey(cluster)) {
                    tempArray = map.get(cluster);
                }
                else {
                    tempArray = new ArrayList<String>();
                }
                
                tempArray.add(host);
                map.put(cluster, tempArray);
            }
            
            for (HashMap.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                ArrayList<String> mapValues = entry.getValue();
                String[] dummp = mapValues.toArray(new String[mapValues.size()]);
                hostClusterMap.put(entry.getKey(), dummp);
            }
                
        } catch (Exception e) {
             e.printStackTrace();
             throw new Exception(e.toString());
        }
        return hostClusterMap;
    }
    
    private ArrayList<String> get(String identifier)
            throws Exception
    {
        try {
            JsonReader jsonReader = getJsonReader(identifier);
            JsonArray arrayList = jsonReader.readObject().getJsonArray(identifier);

            ArrayList<String> responseList = new ArrayList<String>();
            for (int i=0; i<arrayList.size(); i++) {
                JsonObject object = arrayList.getJsonObject(i);
                String name = object.getString("name");
                responseList.add(name);
            }

            return responseList;
                
        } catch (Exception e) {
             e.printStackTrace();
             throw new Exception(e.toString());
        }
    }

    public ArrayList<String> getClusters()
            throws Exception
    {
        return get("omnistack_clusters");
    }
    
    public String[] getTemplates()
            throws Exception
    {
        try {   
            JsonReader vmJson = getJsonReader("virtual_machines?state=ALIVE&show_optional_fields=true");
            JsonArray arrayList = vmJson.readObject().getJsonArray("virtual_machines");

            ArrayList<String> vmList = new ArrayList<String>();
            for (int i=0; i<arrayList.size(); i++) {
                JsonObject object = arrayList.getJsonObject(i);
                boolean isTemplate;
                
                try {
                    isTemplate = object.getString("hypervisor_is_template").equals("true");
                }
                catch (java.lang.ClassCastException e)
                {
                    isTemplate = object.getBoolean("hypervisor_is_template");
                }
                
                if (isTemplate)
                    vmList.add(object.getString("name"));
                
            }
            return vmList.toArray(new String[vmList.size()]);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }
    
    public String[] getArray(String varName)
            throws Exception
    {
        ArrayList <String> list = get(varName);
        return list.toArray(new String[list.size()]);
    }
    
    public ArrayList<String> getVirtualMachines()
            throws Exception
    {
        return get("virtual_machines");
    }
    
    public String[] getAllSvtVMs()
            throws Exception
    {
        try {   
            JsonReader vmJson = getJsonReader("virtual_machines?state=ALIVE");
            JsonArray arrayList = vmJson.readObject().getJsonArray("virtual_machines");

            ArrayList<String> vmList = new ArrayList<String>();
            for (int i=0; i<arrayList.size(); i++) {
                JsonObject object = arrayList.getJsonObject(i);
                vmList.add(object.getString("name"));
            }
            return vmList.toArray(new String[vmList.size()]);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }
    
    public ArrayList<String> getHosts()
            throws Exception
    {
        return get("hosts");
    }
    
    void printList(String name) 
            throws Exception
    {
        System.out.println("Printing list of " + name + " :");
        ArrayList<String> list = get(name);
        for (int i=0; i<list.size(); i++) {
            System.out.println( (i+1) + " : " + list.get(i));
        }
    }
}
