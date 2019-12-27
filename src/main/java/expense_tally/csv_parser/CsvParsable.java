package expense_tally.csv_parser;

import expense_tally.csv_parser.model.CsvTransaction;

import java.io.IOException;
import java.util.List;

public interface CsvParsable {
  // TODO: Refactor to read from a buffer stream so that there isn't a need to unit test the part of reading from a file
  List<CsvTransaction> parseCsvFile(String filePath) throws IOException;
}
