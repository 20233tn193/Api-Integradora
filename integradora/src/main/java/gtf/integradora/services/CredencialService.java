package gtf.integradora.services;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
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

        // ✅ Validar que el torneo esté cerrado
        if (!"cerrado".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("El torneo aún no ha sido cerrado.");
        }

        // ✅ Validar que el pago esté aprobado
        Pago pago = pagoRepository.findByEquipoIdAndTorneoId(equipoId, torneo.getId())
                .orElseThrow(() -> new RuntimeException("No se encontró el pago de inscripción."));

        if (!"pagado".equalsIgnoreCase(pago.getEstatus())) {
            throw new RuntimeException("El pago de inscripción aún no ha sido aprobado.");
        }

        List<Jugador> jugadores = jugadorRepository.findByEquipoIdAndEliminadoFalse(equipoId);

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        for (Jugador jugador : jugadores) {
            // Título
            Paragraph titulo = new Paragraph("CREDENCIAL DE JUGADOR", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            // Datos personales
            document.add(new Paragraph("Nombre: " + jugador.getNombre() + " " + jugador.getApellido()));
            document.add(new Paragraph("CURP: " + jugador.getCurp()));
            document.add(new Paragraph("Nacimiento: " + jugador.getFechaNacimiento()));

            // Foto del jugador
            if (jugador.getFotoUrl() != null) {
                try {
                    @SuppressWarnings("deprecation")
                    Image foto = Image.getInstance(new URL(jugador.getFotoUrl()));
                    foto.scaleToFit(100, 100);
                    foto.setAlignment(Image.ALIGN_RIGHT);
                    document.add(foto);
                } catch (Exception e) {
                    System.out.println("No se pudo cargar la foto del jugador");
                }
            }

            // Logo del equipo
            if (equipo.getLogoUrl() != null) {
                try {
                    @SuppressWarnings("deprecation")
                    Image logo = Image.getInstance(new URL(equipo.getLogoUrl()));
                    logo.scaleToFit(80, 80);
                    document.add(new Paragraph("Equipo: " + equipo.getNombre()));
                    document.add(logo);
                } catch (Exception e) {
                    System.out.println("No se pudo cargar el logo del equipo");
                }
            }

            // Logo y nombre del torneo
            document.add(new Paragraph("Torneo: " + torneo.getNombreTorneo()));
            if (torneo.getLogoSeleccionado() != null) {
                try {
                    Image logoTorneo = Image.getInstance(new URI(torneo.getLogoSeleccionado()).toURL());
                    logoTorneo.scaleToFit(80, 80);
                    document.add(logoTorneo);
                } catch (Exception e) {
                    System.out.println("No se pudo cargar el logo del torneo");
                }
            }

            // Separador
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);
        }

        document.close();
        return baos.toByteArray();
    }
}