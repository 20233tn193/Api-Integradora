package gtf.integradora.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.Torneo;
import gtf.integradora.entity.Pago;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.TorneoRepository;
import gtf.integradora.repository.PagoRepository;

@Service
public class CredencialService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    public byte[] generarCredenciales(String equipoId) throws Exception {
        
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Torneo torneo = torneoRepository.findById(equipo.getTorneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        //Validaciones
        if (!"cerrado".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("El torneo aún no ha sido cerrado.");
        }

        Pago pago = pagoRepository.findByEquipoIdAndTorneoId(equipoId, torneo.getId())
                .orElseThrow(() -> new RuntimeException("No se encontró el pago de inscripción."));

        if (!"pagado".equalsIgnoreCase(pago.getEstatus())) {
            throw new RuntimeException("El pago de inscripción aún no ha sido aprobado.");
        }

        List<Jugador> jugadores = jugadorRepository.findByEquipoIdAndEliminadoFalse(equipoId);
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        @SuppressWarnings("unused")
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        int count = 0;

        for (Jugador jugador : jugadores) {
            PdfPTable credencial = new PdfPTable(1);
            credencial.setWidthPercentage(95);
            credencial.getDefaultCell().setBorder(Rectangle.BOX);
            credencial.getDefaultCell().setPadding(8);

            // Encabezado azul marino
            PdfPCell header = new PdfPCell(new Phrase("CREDENCIAL DE JUGADOR",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE)));
            header.setBackgroundColor(new Color(0, 51, 102));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            header.setBorder(Rectangle.NO_BORDER);
            credencial.addCell(header);

            // Foto del jugador
            if (jugador.getFotoUrl() != null) {
                try {
                    @SuppressWarnings("deprecation")
                    Image foto = Image.getInstance(new URL(jugador.getFotoUrl()));
                    foto.scaleToFit(90, 90);
                    PdfPCell imgCell = new PdfPCell(foto);
                    imgCell.setBorder(Rectangle.NO_BORDER);
                    imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    credencial.addCell(imgCell);
                } catch (Exception e) {
                    credencial.addCell(new Phrase("Foto no disponible"));
                }
            }

            // Nombre, CURP y fecha de nacimiento
            String info = jugador.getNombre() + " " + jugador.getApellido() + "\nCURP: " +
                    jugador.getCurp() + "\nNacimiento: " + jugador.getFechaNacimiento();
            PdfPCell infoCell = new PdfPCell(new Phrase(info, FontFactory.getFont(FontFactory.HELVETICA, 10)));
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.setPaddingTop(4);
            credencial.addCell(infoCell);

            // Escudo del equipo
            if (equipo.getLogoUrl() != null) {
                try {
                    @SuppressWarnings("deprecation")
                    Image logo = Image.getInstance(new URL(equipo.getLogoUrl()));
                    logo.scaleToFit(50, 50);
                    PdfPCell logoCell = new PdfPCell(logo);
                    logoCell.setBorder(Rectangle.NO_BORDER);
                    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    credencial.addCell(logoCell);
                } catch (Exception e) {
                    credencial.addCell(new Phrase("Logo no disponible"));
                }
            }

            // Nombre del equipo
            PdfPCell equipoCell = new PdfPCell(new Phrase("Equipo: " + equipo.getNombre(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            equipoCell.setBorder(Rectangle.NO_BORDER);
            equipoCell.setPaddingTop(3);
            credencial.addCell(equipoCell);

            // Logo del torneo
            if (torneo.getLogoSeleccionado() != null) {
                try {
                    Image logoTorneo = Image.getInstance(new URI(torneo.getLogoSeleccionado()).toURL());
                    logoTorneo.scaleToFit(50, 50);
                    PdfPCell logoTorneoCell = new PdfPCell(logoTorneo);
                    logoTorneoCell.setBorder(Rectangle.NO_BORDER);
                    logoTorneoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    credencial.addCell(logoTorneoCell);
                } catch (Exception e) {
                    credencial.addCell(new Phrase("Logo torneo no disponible"));
                }
            }

            // Nombre del torneo
            PdfPCell torneoCell = new PdfPCell(new Phrase("Torneo: " + torneo.getNombreTorneo(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            torneoCell.setBorder(Rectangle.NO_BORDER);
            torneoCell.setPaddingBottom(6);
            credencial.addCell(torneoCell);

            table.addCell(credencial);
            count++;

            // Nueva página cada 4 credenciales
            if (count % 4 == 0) {
                document.add(table);
                document.newPage();
                table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            }
        }

        if (count % 4 != 0) {
            document.add(table); // Añadir lo restante
        }

        document.close();
        return baos.toByteArray();
    }
}