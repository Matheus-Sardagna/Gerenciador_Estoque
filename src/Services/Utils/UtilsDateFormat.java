package Services.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class UtilsDateFormat {
    private static final DateTimeFormatter PADRAO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static Date parseT(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Data vazia ou nula");
        }

        try {
            java.time.LocalDate ld = LocalDate.parse(dataStr.trim(), PADRAO);

            return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + dataStr);
        }
    }

    public static Date parseDate(java.util.Date data) {
        if (data == null) {
            throw new IllegalArgumentException("Data nula");
        }
        Instant instant = data.toInstant();
        return Date.from(data.toInstant());
    }

    public static LocalDate parse(String data) {
        try {
            return LocalDate.parse(data, PADRAO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + data);
        }
    }
    public static String format(LocalDate data) {
        if (data == null) return "";
        return PADRAO.format(data);
    }

    // Formata java.util.Date para String "dd/MM/yyyy"
    public static String format(Date data) {
        if (data == null) return "";
        LocalDate ld = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return PADRAO.format(ld);
    }
}
