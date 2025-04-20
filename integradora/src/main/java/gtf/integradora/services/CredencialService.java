package gtf.integradora.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.TorneoRepository;

@Service
public class CredencialService {

    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private TorneoRepository torneoRepository;

    public byte[] generarCredenciales(String equipoId) throws Exception {
        System.out.println("🟡 Generando credenciales para equipoId: " + equipoId);

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        System.out.println("✅ Equipo: " + equipo.getNombre());

        Torneo torneo = torneoRepository.findById(equipo.getTorneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        System.out.println("✅ Torneo: " + torneo.getNombreTorneo());

        List<Jugador> jugadores = jugadorRepository.findByEquipoIdAndEliminadoFalse(equipoId);

        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        int count = 0;
        PdfPTable pageTable = new PdfPTable(2);
        pageTable.setWidthPercentage(100);
        pageTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        for (Jugador jugador : jugadores) {
            PdfPTable credencial = new PdfPTable(1);
            credencial.setWidthPercentage(95);
            credencial.getDefaultCell().setBorder(Rectangle.BOX);

            PdfPCell header = new PdfPCell(new Phrase("CREDENCIAL DE JUGADOR",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE)));
            header.setBackgroundColor(new Color(10, 40, 90));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(10);
            header.setBorder(Rectangle.NO_BORDER);
            credencial.addCell(header);

            PdfPTable layout = new PdfPTable(2);
            layout.setWidths(new int[] { 2, 3 });
            layout.setWidthPercentage(100);
            layout.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // FOTO DEL JUGADOR (rotar imagen base64 si es necesario)
            PdfPCell fotoCell;
            try {
                Image foto;
                BufferedImage original;

                if (jugador.getFotoUrl().startsWith("data:image")) {
                    String base64 = jugador.getFotoUrl().split(",", 2)[1];
                    byte[] imageBytes = Base64.getDecoder().decode(base64);
                    BufferedImage baseImg = ImageIO.read(new ByteArrayInputStream(imageBytes));

                    // Forzar rotación 90° a la derecha
                    int w = baseImg.getWidth();
                    int h = baseImg.getHeight();
                    BufferedImage rotated = new BufferedImage(h, w, baseImg.getType());
                    Graphics2D g2d = rotated.createGraphics();
                    g2d.translate(h / 2.0, w / 2.0);
                    g2d.rotate(Math.toRadians(90));
                    g2d.translate(-w / 2.0, -h / 2.0);
                    g2d.drawImage(baseImg, 0, 0, null);
                    g2d.dispose();

                    ByteArrayOutputStream rotatedStream = new ByteArrayOutputStream();
                    ImageIO.write(rotated, "jpg", rotatedStream);
                    foto = Image.getInstance(rotatedStream.toByteArray());
                } else {
                    URL imageUrl = new URL(jugador.getFotoUrl());
                    BufferedImage baseImg = ImageIO.read(imageUrl);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(baseImg, "jpg", stream);
                    foto = Image.getInstance(stream.toByteArray());
                }

                foto.scaleToFit(110, 110);
                fotoCell = new PdfPCell(foto);
            } catch (Exception e) {
                fotoCell = new PdfPCell(new Phrase("Sin foto"));
            }
            fotoCell.setBorder(Rectangle.NO_BORDER);
            fotoCell.setPaddingBottom(10);
            layout.addCell(fotoCell);

            PdfPTable info = new PdfPTable(1);
            info.setWidthPercentage(100);
            info.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font boldBig = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            info.addCell(
                    createNoBorderCell(new Phrase(jugador.getNombre() + " " + jugador.getApellido(), boldBig), 10f));
            info.addCell(createNoBorderCell(new Phrase("CURP: " + jugador.getCurp(), normal), 10f));
            info.addCell(createNoBorderCell(new Phrase("Nacimiento: " + jugador.getFechaNacimiento(), normal), 10f));
            info.addCell(new Paragraph(" "));

            // LOGO EQUIPO
            try {
                Image logoEquipo;
                if (equipo.getLogoUrl().startsWith("data:image")) {
                    String[] parts = equipo.getLogoUrl().split(",");
                    String metadata = parts[0]; // ejemplo: data:image/png;base64
                    String base64Data = parts[1];
                    String formato = "jpg"; // por defecto

                    if (metadata.contains("image/")) {
                        formato = metadata.substring(metadata.indexOf("/") + 1, metadata.indexOf(";"));
                        if (formato.equals("jpeg"))
                            formato = "jpg"; // estandarizar
                    }

                    byte[] logoBytes = Base64.getDecoder().decode(base64Data);
                    BufferedImage logoImg = ImageIO.read(new ByteArrayInputStream(logoBytes));
                    ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
                    ImageIO.write(logoImg, formato, logoStream);
                    logoEquipo = Image.getInstance(logoStream.toByteArray());
                } else {
                    logoEquipo = Image.getInstance(new URL(equipo.getLogoUrl()));
                }

                logoEquipo.scaleToFit(60, 60);
                PdfPCell logoCell = new PdfPCell(logoEquipo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                info.addCell(logoCell);
                
            } catch (Exception e) {
                info.addCell(createNoBorderCell(new Phrase("Logo equipo no disponible", normal), 10f));
            }

            info.addCell(createNoBorderCell(new Phrase("        " + equipo.getNombre(), bold), 10f));

            // LOGO TORNEO
            try {
                Image logoTorneo = Image.getInstance(new URI(torneo.getLogoSeleccionado()).toURL());
                logoTorneo.scaleToFit(60, 60);
                PdfPCell logoTorneoCell = new PdfPCell(logoTorneo);
                logoTorneoCell.setBorder(Rectangle.NO_BORDER);
                logoTorneoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                info.addCell(logoTorneoCell);
            } catch (Exception e) {
                info.addCell(createNoBorderCell(new Phrase("Logo torneo no disponible", normal), 10f));
            }

            info.addCell(createNoBorderCell(new Phrase("        " + torneo.getNombreTorneo(), bold), 10f));

            PdfPCell infoCell = new PdfPCell(info);
            infoCell.setBorder(Rectangle.NO_BORDER);
            layout.addCell(infoCell);

            credencial.addCell(layout);
            pageTable.addCell(credencial);
            count++;

            if (count % 4 == 0) {
                document.add(pageTable);
                document.newPage();
                pageTable = new PdfPTable(2);
                pageTable.setWidthPercentage(100);
                pageTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            }
        }

        if (count % 4 != 0) {
            for (int i = 0; i < (4 - (count % 4)); i++) {
                pageTable.addCell(new PdfPCell(new Phrase(" ")) {
                    {
                        setBorder(Rectangle.NO_BORDER);
                    }
                });
            }
            document.add(pageTable);
        }

        document.close();
        return baos.toByteArray();
    }

    private PdfPCell createNoBorderCell(Phrase phrase, float paddingLeft) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(3);
        cell.setPaddingLeft(paddingLeft); // <-- aquí lo aplicas
        return cell;
    }

    private BufferedImage corregirOrientacionVertical(BufferedImage img) {
        if (img.getHeight() > img.getWidth()) {
            BufferedImage rotated = new BufferedImage(img.getHeight(), img.getWidth(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = rotated.createGraphics();
            AffineTransform at = new AffineTransform();
            at.translate(rotated.getWidth() / 2.0, rotated.getHeight() / 2.0);
            at.rotate(Math.toRadians(90));
            at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);
            g2d.setTransform(at);
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return rotated;
        } else {
            return img;
        }
    }
}