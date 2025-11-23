package Services.Utils;

import java.io.File;
import java.io.IOException;

public class FileUtilsService {

    public static File getOrCreateFile(String caminho) throws IOException {
        File file = new File(caminho);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            boolean criado = file.createNewFile();
            if (!criado) {
                throw new IOException("Não foi possível criar o arquivo: " + caminho);
            }
        }

        return file;
    }
}
