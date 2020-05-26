package expense_tally.csv_parser.service;

import expense_tally.csv_parser.model.CsvTransaction;

import java.io.IOException;
import java.util.List;

public interface CsvParsable {
  // TODO: Refactor to read from a buffer stream so that there isn't a need to unit test the part of reading from a file
  /**
   * Read the content of a CSV file in the pre-defined format from the file with the directory <i>filePath</i>.
   * <p>Each line inside the comma separated value (csv) file is a single transaction record</p>
   * @param filePath location of the csv file. file path can be relative or absolutely path.
   * @return list of transaction extracted from the csv file
   * @throws IOException when there is issue reading the filepath
   */
  List<CsvTransaction> parseCsvFile(String filePath) throws IOException;
}
