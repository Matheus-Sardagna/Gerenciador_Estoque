package Services.Utils;

import javax.swing.text.*;

public class DateMaskFilter extends DocumentFilter {

    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null) {
            string = aplicarMascara(fb, string);
        }
        super.insertString(fb, 0, string, attr); // sempre reinicia do início
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null) {
            text = aplicarMascara(fb, text);
        }
        super.replace(fb, 0, fb.getDocument().getLength(), text, attrs); // substitui tudo
    }

    private String aplicarMascara(FilterBypass fb, String text) throws BadLocationException {
        // Junta o texto existente com o novo
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String full = (atual + text).replaceAll("\\D", ""); // só dígitos

        // Limita a 8 dígitos (ddMMyyyy)
        if (full.length() > 8) full = full.substring(0, 8);

        // Insere barras
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < full.length(); i++) {
            sb.append(full.charAt(i));
            if (i == 1 || i == 3) sb.append("/");
        }

        return sb.toString();
    }
}
