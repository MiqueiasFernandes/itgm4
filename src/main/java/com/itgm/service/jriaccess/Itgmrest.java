package com.itgm.service.jriaccess;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by mfernandes on 23/06/17.
 */
public class Itgmrest {

    private static final String SERVER = "http://itgm.mikeias.net:8080/";
    private static final String SERVICE = SERVER + "ITGMRest2/webresources/jriaccess/";
//    @GET
    private static final String PATH_GET_PROCESSOS = SERVICE + "process/";
    private static final String PATH_GET_COMPARTILHAMENTO = SERVICE + "compartilhamento/";
    private static final String PATH_GET_COMPARTILHADO = SERVICE + "compartilhado/"; ///compartilhado/{usuario}/{token}
    private static final String PATH_GET_STATUS = SERVICE + "status/"; ///status/{token}
    private static final String PATH_GET_LIST = SERVICE + "list/"; ///list/{usuario}/{projeto}/{cenario}/{diretorio}
    private static final String PATH_GET_CONTENT = SERVICE + "content/"; ///"content/{usuario}/{projeto}/{cenario}/{diretorio}/{file}"
    private static final String PATH_GET_FILE = SERVICE + "file/"; ///file/{usuario}/{projeto}/{cenario}/{diretorio}/{file}
//    @PUT
    private static final String PATH_PUT_SUSPENDER = SERVICE + "suspend/";
    private static final String PATH_PUT_RESUMIR = SERVICE + "resume/";
    private static final String PATH_PUT_PARAR = SERVICE + "stop/";
//    @POST
    private static final String PATH_POST_PROCESSO = SERVICE + ""; ///{usuario}/{projeto}/{cenario}/{diretorio}
    private static final String PATH_POST_UPDATE = SERVICE + "update/";
    private static final String PATH_POST_DIRETORIO = SERVICE + "diretorio/";
    private static final String PATH_POST_FILE = SERVICE + ""; ///{usuario}/{projeto}/{cenario}/{diretorio}/{file}
//    @DELETE
    private static final String PATH_DELETE_PROCESSO = SERVICE + "process/"; ///process/{token}
    private static final String PATH_DELETE_USUARIO = SERVICE + "usuario/"; ///usuario/{usuario}
    private static final String PATH_DELETE_PROJETO = SERVICE + "projeto/"; ///projeto/{usuario}/{projeto}
    private static final String PATH_DELETE_CENARIO = SERVICE + "cenario/"; ///cenario/{usuario}/{projeto}/{cenario}
    private static final String PATH_DELETE_DIRETORIO = SERVICE + "diretorio/"; ///diretorio/{usuario}/{projeto}/{cenario}/{diretorio}
    private static final String PATH_DELETE_ARQUIVO = SERVICE + "file/"; ///file/{usuario}/{projeto}/{cenario}/{diretorio}/{file}

    private static String buidURIforGET_COMPARTILHAMENTO(String origem, String destino) {
        return PATH_GET_COMPARTILHAMENTO + "?origem=" +origem  + "&destino=" + destino;
    }
    private static String buidURIforGET_COMPARTILHADO(String usuario, String token) {
        return PATH_GET_COMPARTILHADO + usuario  + "/" + token;
    }
    private static String buidURIforGET_STATUS(String usuario, String token) {
        return PATH_GET_STATUS + usuario  + "/" + token;
    }
    private static String buidURIforGET_LIST(String usuario, String projeto, String cenario, String diretorio) {
        return PATH_GET_LIST + usuario  + "/" + projeto + "/" + cenario + "/" + diretorio;
    }
    private static String buidURIforGET_CONTENT(String usuario,
                                                String projeto,
                                                String cenario,
                                                String diretorio,
                                                String file,
                                                String subdiretorio,
                                                boolean criptografar) {
        return PATH_GET_CONTENT + usuario  + "/"
            + projeto + "/"
            + cenario + "/"
            + diretorio
            + "?subdiretorio=" + subdiretorio
            + "&cript=" + criptografar;
    }
    private static String buidURIforGET_FILE(
        String usuario,
        String projeto,
        String cenario,
        String diretorio,
        String subdiretorio,
        boolean returnMeta,
        boolean isImage) {
        return PATH_GET_FILE + usuario  + "/" +
            projeto + "/" +
            cenario + "/" +
            diretorio
            + "?subdiretorio=" + subdiretorio
            + "&meta=" + returnMeta
            + "&image=" + isImage;
    }
    private static String buidURIforPOST_PROCESSO(String usuario,
                      String projeto,
                      String cenario,
                      String diretorio) {
        return PATH_POST_PROCESSO + usuario  + "/" + projeto + "/" + cenario + "/" + diretorio;
    }
    private static String buidURIforDELETE_PROCESSO(String token) {
        return SERVICE + PATH_DELETE_PROCESSO + token;
    }
    private static String buidURIforDELETE_USUARIO(String usuario) {
        return SERVICE + PATH_DELETE_USUARIO + usuario;
    }
    private static String buidURIforDELETE_PROJETO(String usuario, String projeto) {
        return PATH_DELETE_PROJETO + usuario  + "/" + projeto;
    }
    private static String buidURIforDELETE_CENARIO(String usuario, String projeto, String cenario) {
        return PATH_DELETE_CENARIO + usuario  + "/" + projeto + "/" + cenario;
    }
    private static String buidURIforDELETE_DIRETORIO(String usuario, String projeto, String cenario, String diretorio) {
        return PATH_DELETE_DIRETORIO + usuario  + "/" + projeto + "/" + cenario + "/" + diretorio;
    }
    private static String buidURIforDELETE_ARQUIVO(String usuario,
                                    String projeto,
                                    String cenario,
                                    String diretorio,
                                    String subdiretorio) {
        return PATH_DELETE_ARQUIVO + usuario  + "/" + projeto + "/" + cenario + "/" + diretorio + "?subdiretorio=" + subdiretorio;
    }
    private static String buidURIforPOST_FILE(String usuario,
                               String projeto,
                               String cenario,
                               String diretorio,
                               String file,
                               String subdiretorio) {
        return PATH_POST_FILE +
            usuario  + "/" +
            projeto + "/" +
            cenario + "/" +
            diretorio + "/" +
            file+ "?subdiretorio=" + subdiretorio;
    }

    private static Object getOnTemplate(String path, Class classe, Object retorno) {
        System.out.println("#######################GET " + path +"############################");
        try {
            return getRestTemplate().getForObject( path,  classe);
        }catch (Exception e) {
            System.err.println("ERRO AO TENTAR: GET " + path + " EX: " + e);
        }
        return retorno;
    }

    private static Object postOnTemplate(String path, Object enviado, Class classe, Object retorno) {
        System.out.println("#######################POST " + path +"############################");
        try {
            return getRestTemplate().postForObject(
                path, enviado,  classe);
        }catch (Exception e) {
            System.err.println("ERRO AO TENTAR: POST " + path + " EX: " + e);
        }
        return retorno;
    }

    private static boolean deleteOnTemplate(String path) {
        System.out.println("#######################DELETE " + path +"############################");
        try {
            getRestTemplate().delete(path);
            return true;
        }catch (Exception e) {
            System.err.println("ERRO AO TENTAR: POST " + path + " EX: " + e);
        }
        return false;
    }

    private static RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    private static boolean postTEXTO(String path, String enviado) {
        return "true".equals(
            postOnTemplate(path, enviado, String.class, "false")
        );
    }

    private static boolean postBINARIO(String path, MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setAccept(Arrays.asList(new MediaType[]{MediaType.TEXT_PLAIN}));
            HttpEntity<byte[]> entity = null;
            entity = new HttpEntity<byte[]>(
                file.getBytes(),
                headers);
            return "true".equals(
                postOnTemplate(path, entity, String.class, "false")
            );
        }catch (Exception ex){
            System.err.println("ERRO AO TENTAR SERIALIZAR EX: " + ex);
        }
        return false;
    }



    public static void criarUsuario(String login, String data) {
        postTEXTO(buidURIforPOST_FILE(login,
            "*",
            "*",
            "*",
            "user.data",
            "desc/"), data);
    }

    public static void criarProjeto(String login, String projeto, String data) {
        postTEXTO(buidURIforPOST_FILE(login,
            projeto,
            "*",
            "*",
            "projeto.data",
            "desc/"), data);
    }

    public static void criarCenario(String login, String projeto, String cenario, String data) {
        postTEXTO(buidURIforPOST_FILE(login,
            projeto,
            cenario,
            "*",
            "cenario.data",
            "desc/"), data);
    }

    public static void criarDiretorio(String login, String projeto, String cenario, String diretorio, String data) {
        postTEXTO(buidURIforPOST_FILE(login,
            projeto,
            cenario,
            diretorio,
            "diretorio.data",
            "desc/"), data);
    }

    public static boolean isServerAlive() {
      String retorno = (String) getOnTemplate(PATH_GET_PROCESSOS, String.class, null);
        return retorno.startsWith("{\"process\":[") && retorno.endsWith("]}");
    }

    public static boolean createNewFile(String user,
                                        String projeto,
                                        String cenario,
                                        String diretorio,
                                        String subdiretorio,
                                        String nomeDoArquivo,
                                        String conteudo) {
        return postTEXTO(
            buidURIforPOST_FILE(user, projeto, cenario, diretorio, nomeDoArquivo, subdiretorio),
            conteudo);
    }

    public static boolean postBinario(String usuario,
                                      String projeto,
                                      String cenario,
                                      String diretorio,
                                      String file,
                                      String subdiretorio,
                                      MultipartFile conteudo) {
        return postBINARIO(
            buidURIforPOST_FILE(usuario, projeto, cenario, diretorio, file, subdiretorio),
            conteudo
        );
    }

//    private static File convert(MultipartFile file) {
//        File convFile = new File(file.getOriginalFilename());
//        try {
//            convFile.createNewFile();
//            FileOutputStream fos;
//            fos = new FileOutputStream(convFile);
//            fos.write(file.getBytes());
//            fos.close();
//        } catch (Exception ex) {
//            System.err.println("not saving file: " + ex);
//            return null;
//        }
//        return convFile;
//    }


    public static String listFiles(String usuario, String projeto, String cenario, String diretorio) {
        return (String) getOnTemplate(
            buidURIforGET_LIST(usuario, projeto, cenario, diretorio),
            String.class,
            ""
        );
    }

    public static void removeDIR(
        String usuario,
        String projeto,
        String cenario,
        String diretorio,
        String file,
        String subdiretorio) {

        String path = usuario;

        if (projeto == null && cenario == null && diretorio == null && file == null) {
            path = "usuario/" + usuario;
        } else if (cenario == null && diretorio == null && file == null) {
            path = "projeto/" + usuario + "/" + projeto;
        } else if (diretorio == null && file == null) {
            path = "cenario/" + usuario + "/" + projeto + "/" + cenario;
        } else if (file == null) {
            path = "diretorio/" + usuario + "/" + projeto + "/" + cenario + "/" + diretorio;
        } else {
            path = "file/" + usuario + "/" + projeto + "/" + cenario + "/" + diretorio + "/" + file;
        }
        deleteOnTemplate(SERVICE + path + "?subdiretorio=" + (subdiretorio != null ? subdiretorio : ""));
    }

    public static void executarBatch(String usuario,
                                     String projeto,
                                     String cenario,
                                     String diretorio,
                                     String codigo){
        String query = "?&parametros=BATCH" +
            "&parametros=log.txt" +
            "&parametros=INFO" +
            "&memoria=20" +
            "&cpu=1" +
            "&disco=20" +
            "&salvar=true";
        try {
            postOnTemplate(
                SERVICE + usuario + "/" +  projeto + "/" +  cenario + "/" +  diretorio + "/" +  query,
                java.net.URLEncoder.encode(codigo, "UTF-8"),
                String.class,
                null
            );
        }catch (Exception ex){

        }
    }


    public static String criarCompartilhamento(String origem, String destino){
        return (String) getOnTemplate(
            buidURIforGET_COMPARTILHAMENTO(origem, destino),
            String.class,
            "");
    }

    public static String receberCompartilhamento(String usuario, String token){
        return (String) getOnTemplate(buidURIforGET_COMPARTILHADO(usuario, token),
            String.class,
            "");
    }

    public static boolean createFileCompartilhamento(
        String user,
        String token,
        String local
    ){
        String conteudo = local + "\n\n";
        return createNewFile(
            user,
            "*",
            "*",
            "*",
            "share/" + token + "/",
            token,
            conteudo);
    }


    public static String getToken(String usuario,
                                  String projeto,
                                  String cenario,
                                  String diretorio,
                                  String[] parametros,
                                  int memoria,
                                  int disco,
                                  int cpu,
                                  boolean salvar,
                                  String codigo) {
        String params = "?parametros=" + String.join("&parametros=", parametros) +
            "&memoria=" + memoria +
            "&cpu=" + cpu +
            "&disco="+  disco +
            "&salvar=" + (salvar ? "true" : "false");
        try {
            return (String) postOnTemplate(
                buidURIforPOST_PROCESSO(usuario, projeto, cenario, diretorio) + params,
                java.net.URLEncoder.encode(codigo, "UTF-8"),
                String.class,
                "");
        }catch (Exception ex){
            System.err.println("ERRO AO POST PROCESS " + ex);
        }
        return "";
    }

    public static String publicFile(
        String usuario,
        String projeto,
        String cenario,
        String diretorio,
        String subdiretorio,
        String file,
        boolean meta,
        boolean image) {
        String ret = (String) getOnTemplate(
            buidURIforGET_FILE(usuario, projeto, cenario, diretorio, subdiretorio, meta, image),
            String.class,
            ""
        ) ;
        return (ret != null && ret.length() > 0 && !ret.isEmpty() && !ret.startsWith("error:")) ? ret : null;
    }



}
