package esprit.tn.traningmsproject;

import com.lowagie.text.pdf.PdfPTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.List;

@Service
public class TrainingService {


    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training addTraining(Training training) {
        return trainingRepository.save(training);
    }

    public List<Training> getAll(){
        return trainingRepository.findAll();
    }

    public Training getTrainingById(int id) {
        return trainingRepository.findById(id).orElse(null);
    }

    public String deleteTraining(int id) {
        if (trainingRepository.findById(id).isPresent()) {
            trainingRepository.deleteById(id);
            return "Training supprimé";
        } else
            return "Training non supprimé";
    }
    public Training updateTraining(int id, Training newTraining) {
        if (trainingRepository.findById(id).isPresent()) {

            Training existingTraining = trainingRepository.findById(id).get();
            existingTraining.setTitle(newTraining.getTitle());
            existingTraining.setLevel(newTraining.getLevel());
            existingTraining.setDescription(newTraining.getDescription());
            existingTraining.setTypeTraining(newTraining.getTypeTraining());

            return trainingRepository.save(existingTraining);
        } else
            return null;
    }

    public List<Training> getTrainingsByLevel(String level) {
        return trainingRepository.findByLevel(level);
    }

    public List<Training> getTrainingsByType(TypeTraining typeTraining) {
        return trainingRepository.findByTypeTraining(typeTraining);
    }

    public List<Training> searchTrainingsByTitle(String keyword) {
        return trainingRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Training> getTrainingsByLevelAndType(String level, TypeTraining typeTraining) {
        return trainingRepository.findByLevelAndTypeTraining(level, typeTraining);
    }


    public Page<Training> getTrainingsWithPaginationAndSorting(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return trainingRepository.findAll(pageable);
    }

    public List<Training> searchTrainingsByTitleAndLevel(String keyword, String level) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndLevel(keyword, level);
    }

    public List<Training> searchTrainingsByTitleAndType(String keyword, TypeTraining typeTraining) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndTypeTraining(keyword, typeTraining);
    }

    public List<Training> searchByTitleLevelAndType(String keyword, String level, TypeTraining type) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndLevelAndTypeTraining(keyword, level, type);
    }


    public ByteArrayInputStream exportTrainingsToPDF(List<Training> trainings) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Liste des Formations Filtrées", titleFont));
            document.add(new Paragraph(" ")); // espace

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Header
            table.addCell(new Phrase("Title", headerFont));
            table.addCell(new Phrase("Level", headerFont));
            table.addCell(new Phrase("Description", headerFont));
            table.addCell(new Phrase("Type", headerFont));

            // Body
            for (Training t : trainings) {
                table.addCell(new Phrase(t.getTitle(), bodyFont));
                table.addCell(new Phrase(t.getLevel(), bodyFont));
                table.addCell(new Phrase(t.getDescription(), bodyFont));
                table.addCell(new Phrase(t.getTypeTraining().toString(), bodyFont));
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    public String getTrainingReport() {
        StringBuilder report = new StringBuilder();

        report.append("Nombre de formations par type :\n");
        List<Object[]> typeCounts = trainingRepository.countTrainingsByType();
        for (Object[] row : typeCounts) {
            report.append("- ").append(row[0]).append(" : ").append(row[1]).append("\n");
        }

        report.append("\nNombre de formations par niveau :\n");
        List<Object[]> levelCounts = trainingRepository.countTrainingsByLevel();
        for (Object[] row : levelCounts) {
            report.append("- ").append(row[0]).append(" : ").append(row[1]).append("\n");
        }

        return report.toString();
    }


    public ByteArrayInputStream generateTrainingSummaryPDF() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Rapport des Formations", titleFont));
            document.add(new Paragraph(" ")); // espace

            // Formations par type
            document.add(new Paragraph("Nombre de formations par type :", sectionFont));
            List<Object[]> typeCounts = trainingRepository.countTrainingsByType();
            for (Object[] row : typeCounts) {
                String line = "- " + row[0] + " : " + row[1];
                document.add(new Paragraph(line, textFont));
            }

            document.add(new Paragraph(" ")); // espace

            // Formations par niveau
            document.add(new Paragraph("Nombre de formations par niveau :", sectionFont));
            List<Object[]> levelCounts = trainingRepository.countTrainingsByLevel();
            for (Object[] row : levelCounts) {
                String line = "- " + row[0] + " : " + row[1];
                document.add(new Paragraph(line, textFont));
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }



}
