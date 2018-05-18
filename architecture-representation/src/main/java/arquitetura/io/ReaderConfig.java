package arquitetura.io;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

/**
 * Classe responsável por acesso ao arquivo de configuração
 * <b>application.yaml</b>/
 * 
 * @author edipofederle<edipofederle@gmail.com>
 * 
 */
public class ReaderConfig {

    static Logger LOGGER = LogManager.getLogger(ReaderConfig.class.getName());
    private final static String PATH_CONFIGURATION_FILE = "config/application.yaml";
    private static DirTarget dir;

    private static String dirTarget;
    private static String dirExportTarget;
    private static String pathToProfileSMarty;
    private static String pathToProfileConcerns;
    private static String pathToTemplateModelsDirectory;
    private static String pathToProfileRelationships;
    private static String pathToProfilePatterns;

    public static String newPathToConfigurationFile;

    public static void setPathToConfigurationFile(String newPath) {
	newPathToConfigurationFile = newPath;
    }

    /**
     * Diretorio onde a arquitetura sera salva para manipulacao Este diretorio
     * pode ser qualquer um com acesso de escrita e leitura.
     * 
     * @return
     */
    public static String getDirTarget() {
	if (dirTarget != null)
	    return dirTarget;
	return dir.getDirectoryToSaveModels();
    }

    /**
     * Diretório onde a arquitetura será exportada para que possa ser utilizada.
     * Resultado final
     * 
     * @return
     */
    public static String getDirExportTarget() {
	if (dirExportTarget != null)
	    return dirExportTarget;
	return dir.getDirectoryToExportModels();
    }

    /**
     * Path pra o arquivo de profile do SMarty
     * 
     * @return
     */
    public static String getPathToProfileSMarty() {
	if (pathToProfileSMarty != null)
	    return pathToProfileSMarty;
	return dir.getPathToProfile();
    }

    /**
     * Path para o arquivo de profile contendo os concerns.
     * 
     * @return
     */
    public static String getPathToProfileConcerns() {
	if (pathToProfileConcerns != null)
	    return pathToProfileConcerns;
	return dir.getPathToProfileConcern();
    }

    /**
     * Path para um diretorio contendo os tres arquivos que são usados como
     * template para geração da arquitetura. Estes arquivos contem somente um
     * esqueleto. Estes arquivos se encontram na raiz do projeto na pasta
     * filesTemplates. Você pode copiar os arquivos e colocar em qualquer
     * diretório com permissão de leitura.
     * 
     * @return
     */
    public static String getPathToTemplateModelsDirectory() {
	if (pathToTemplateModelsDirectory != null)
	    return pathToTemplateModelsDirectory;
	return dir.getPathToTemplateModelsDirectory();
    }

    /**
     * Verifica se existe o perfil smarty configurado
     * 
     * @return boolean
     */
    public static boolean hasSmartyProfile() {
	return getPathToProfileSMarty().isEmpty() || getPathToProfileSMarty() == null ? false : true;
    }

    /**
     * Verifica se existe o perfil concerns configurado
     * 
     * @return boolean
     */
    public static boolean hasConcernsProfile() {
	return getPathToProfileConcerns().isEmpty() || getPathToProfileConcerns() == null ? false : true;
    }

    /**
     * Verifica se existe o perfil concerns configurado
     * 
     * @return boolean
     */
    public static boolean hasRelationsShipProfile() {
	return getPathToProfileRelationships().isEmpty() || getPathToProfileRelationships() == null ? false : true;
    }

    public static boolean hasPatternsProfile() {
	return getPathToProfilePatterns().isEmpty() || getPathToProfilePatterns() == null ? false : true;
    }

    public static void setDirTarget(String path) {
	dirTarget = path;
    }

    public static void setDirExportTarget(String path) {
	dirExportTarget = path;
    }

    public static void setPathToProfileSMarty(String path) {
	pathToProfileSMarty = path;
    }

    public static void setPathToProfileConcerns(String path) {
	pathToProfileConcerns = path;
    }

    public static void setPathToTemplateModelsDirectory(String path) {
	pathToTemplateModelsDirectory = path;
    }

    public static String getPathToProfileRelationships() {
	if (pathToProfileRelationships != null)
	    return pathToProfileRelationships;
	return dir.getPathToProfileRelationships();
    }

    public static void setPathProfileRelationship(String path) {
	pathToProfileRelationships = path;
    }

    public static String getPathToProfilePatterns() {
	if (pathToProfilePatterns != null)
	    return pathToProfilePatterns;
	return dir.getPathToProfilePatterns();
    }
    
    public static void setPathToProfilePatterns(String path) {
	pathToProfilePatterns = path;
    }

    public static String getNewPathToConfigurationFile() {
	return newPathToConfigurationFile;
    }

    public static void setNewPathToConfigurationFile(String newPath) {
	newPathToConfigurationFile = newPath;
    }
    
    /**
     * Default path is config/application.yaml
     * If you need chance it, before call this method set the new path with <code>setNewPathToConfigurationFile</code>
     * <br/><br/>
     * IMPORTANT: Only call this method once.
     */
    public static void load() {
	try {
	    if (newPathToConfigurationFile != null)
		dir = Yaml.loadType(new File(newPathToConfigurationFile), DirTarget.class);
	    else
		dir = Yaml.loadType(new File(PATH_CONFIGURATION_FILE), DirTarget.class);
	} catch (FileNotFoundException e) {
	    LOGGER.info("I can't read the configuration file at: " + PATH_CONFIGURATION_FILE);
	}
    }

}