package gtf.integradora.services;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.Pago;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.PagoRepository;
import gtf.integradora.repository.TorneoRepository;

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
            document.add(new Paragraph("Nombre: " + jugador.getNombre() + " " + jugador.getApellido()));
            document.add(new Paragraph("CURP: " + jugador.getCurp()));
            document.add(new Paragraph("Nacimiento: " + jugador.getFechaNacimiento()));

            if (jugador.getFotoUrl() != null) {
                Image foto = Image.getInstance(new URL(jugador.getFotoUrl()));
                foto.scaleToFit(100, 100);
                document.add(foto);
            }

            if (equipo.getLogoUrl() != null) {
                Image logo = Image.getInstance(new URL(equipo.getLogoUrl()));
                logo.scaleToFit(80, 80);
                document.add(logo);
            }

            document.add(new Paragraph("Torneo: " + torneo.getNombreTorneo()));

            if (torneo.getLogoSeleccionado() != null) {
                Image logoTorneo = Image.getInstance(new URI(torneo.getLogoSeleccionado()).toURL());
                logoTorneo.scaleToFit(80, 80);
                document.add(logoTorneo);
            }

            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
        }

        document.close();
        return baos.toByteArray(); // ✅ Aquí va el buffer real
    }
}