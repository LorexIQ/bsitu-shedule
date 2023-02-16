package com.example.sheduler.parser;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.read.biff.BiffException;

public class BSITU {
    private static final String url = "http://www.bgitu.ru"; // Главный домен сайта
    private static final String urlGroups = url + "/studentu/raspisanie/ochnoe-obuchenie/"; // Страница с файлами расписания
    private static final String urlShedule = url + "/studentu/raspisanie/"; // Страница с графиком пар
    private static final String[] daysWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"}; // Дни недели
    private static final JSONArray pairsShedule = new JSONArray(); // Список графика пар

    public static void main() {
        new ParseData().execute(); // Вызов асинхронного потока
    }

    static class ParseData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ArrayList<JSONObject> resultParse = this.parseSiteDataToGroupsData(); // Вызов функции парсера
                System.out.println(resultParse); // Выводим количество
            } catch (IOException e) {
                throw new RuntimeException(e); // Обработка исключений парсера
            }
            return null;
        }

        // Функция форматирования строк для подготовки к regex обработкам
        private String getFormattedCell(Cell cell) {
            return (cell.getContents() + ".") // Добавление точек в конец, для правильного написания ФИО
                    .replace("\n", " ") // Удаление переносов
                    .replace("_", "") // Удаление нижних подчёркиваний
                    .replaceAll("\\.+", ".") // Замена последовательностей точек на один символ
                    .replaceAll("\\(.*\\)", " ") // Удаление всес блоков слов закрытыми скобками
                    .replaceAll("\\s{2,}", " ") // Замена последовательностей пробелов на один символ
                    .replaceAll("\\s+\\.+", "") // Удаление последовательностей пробелов и точек в совокупе
                    .trim(); // Удаление пробелов вокруг строки
        }

        // Возврат значения борта. Исключение возвращает 0
        private int getBorderLineValue(Cell cell, Border border) {
            try {
                return cell.getCellFormat().getBorderLine(border).getValue();
            } catch (NullPointerException e) {
                return 0;
            }
        }

        // Проверяем наличие значения в строке
        private Boolean checkColValue(Cell cell) {
            return cell != null && !cell.getContents().equals("");
        }

        // Фукнция парсинга сайта
        private ArrayList<JSONObject> parseSiteDataToGroupsData() throws IOException {
            this.parseSiteDataToPairsShedule(); // Вызов парсинга графика пар
            Document document = Jsoup.connect(urlGroups).get(); // Получение страницы с файлами расписания
            Elements tables = document.select("table"); // Получение таблиц
            ArrayList<JSONObject> firstTerm = this.parseTermDataToObject(tables.get(0).select("a")); // Парсинг первого семетра
            ArrayList<JSONObject> secondTerm = this.parseTermDataToObject(tables.get(1).select("a")); // Парсинг второго семетра
            return firstTerm.size() == 0 ? secondTerm : firstTerm; // Возврат текущего семестра
        }

        // Функция парсинга сайта расписания
        private void parseSiteDataToPairsShedule() {
            try {
                Document document = Jsoup.connect(urlShedule).get(); // Получение страницы с расписанием
                Elements firstWeek = document.select("tr > td:nth-child(2)"); // Получение таблицы первого семетра
                Elements secondWeek = document.select("tr > td:last-child"); // Получение таблицы второго семетра

                // Чтение дней всех недель
                for (int i = 0; i < firstWeek.size(); i++) {
                    // Чтение дня первой недели
                    final String[] firstWeekData = firstWeek.get(i).text()
                            .replace('.', ':').split("-");
                    // Чтение дня второй недели
                    final String[] secondWeekData = secondWeek.get(i).text()
                            .replace('.', ':').split("-");
                    // Запись расписания одного дня в массив
                    pairsShedule.put(new JSONObject()
                            .put("timeStart1", firstWeekData[0])
                            .put("timeEnd1", firstWeekData[1])
                            .put("timeStart2", secondWeekData[0])
                            .put("timeEnd2", secondWeekData[1])
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        // Обработка html таблицы файлов расписаний
        private ArrayList<JSONObject> parseTermDataToObject(Elements termData) {
            // Regex для получения [Cabinet FIO], [Cabinet FIO] из читаемой из таблицы файла строки
            final Pattern patternFIO = Pattern.compile("((\\S+\\s\\s\\S+\\s)|(\\S+\\s)\\S* *.\\..\\.?, )*((\\S+\\s\\s\\S+\\s)|(\\S+\\s)\\S* *.\\..\\.?)$");
            // Regex для чтения номера группы из Regex выше
            final Pattern patternGroup = Pattern.compile("^((\\S+\\s/\\s\\S+)|(\\S+))");
            final ArrayList<JSONObject> groupsShedule = new ArrayList<>(); // Список пар для каждой из групп

            // Проход по всей таблице
            for (Element element : termData) {
                final String filePath = url + element.attr("href"); // Получение путь к файлу расписания

                try {
                    // Через поток буфера читаем excel файл и создаём его объект
                    final Workbook workbook = Workbook.getWorkbook(new BufferedInputStream(new URL(filePath).openStream()));
                    // Получение таблицы из объекта excel
                    final Sheet sheet = workbook.getSheet(0);

                    // Проход по всем столбцам таблицы, начиная с первой группы
                    for (int columnIndex = 3; columnIndex < sheet.getColumns(); columnIndex++) {
                        Cell cell = sheet.getCell(columnIndex, 1); // Чтение ткущей ячейки группы
                        Cell cellPrev = sheet.getCell(columnIndex - 1, 1); // Чтение предыдущей ячейки группы
                        Cell downRowCell = sheet.getCell(columnIndex, 2);; // Чтение нижней ячейки группы
                        if (!checkColValue(cell)) { // Если текущая ячейка пустая
                            // Если предыдушая и нижняя ячейки пустый - следуящая итерация
                            // Иначе в текущую ячейку записываем дынные предыдущей
                            if (!checkColValue(downRowCell) || !checkColValue(cellPrev)) continue; else cell = cellPrev;
                        }
                        // Чтение имени группы
                        final String nameGroup = cell.getContents() +
                                (downRowCell == null ? "" : downRowCell.getContents());
                        // Добавляем объект группы в массив
                        groupsShedule.add(new JSONObject()
                                .put("name", nameGroup)
                                .put("url", filePath)
                                .put("days", new JSONArray()));
                        // Получение дней (ссылка), добавленных в последнюю группу
                        final JSONArray groupSheduleDays = groupsShedule.get(groupsShedule.size() - 1).getJSONArray("days");

                        // Чтение строк каждой из групп от начала учебных дней - до конца таблицы
                        for (int rowIndex = 3; rowIndex < sheet.getRows(); rowIndex++) {
                            // Чтение опорной ячейки, для определения границ дня
                            Cell rowCellDayWeek = sheet.getCell(2, rowIndex);
                            // Если ячейка пустая или нет левого борта (Конец недели), завершаем чтение группы
                            if (rowCellDayWeek == null || this.getBorderLineValue(rowCellDayWeek, Border.LEFT) == 0) break;

                            // Чтение текущей ячейки
                            Cell rowCell = sheet.getCell(columnIndex, rowIndex);
                            // Чтение следующей ячейки
                            Cell rowCellNext = sheet.getCell(columnIndex, rowIndex + 1);
                            // Чтение кол-ва записанных дней в объекте группы из ссылки
                            final int count = groupSheduleDays.length();

                            // Если ячейка дня недели не пустая
                            if (rowCellDayWeek != null) {
                                if (count == 0) { // если дней в объекте нет
                                    // Добавляем первый день недели
                                    groupSheduleDays.put(new JSONObject()
                                            .put("name", daysWeek[count]) // Имя дня
                                            .put("pairs", new JSONArray()) // Все пары
                                            .put("startRow", rowIndex)); // Номер строки начала дня
                                } else if (count != 6 && // Иначе если не 6 дней в объекте и разница между текущей
                                        (int) groupSheduleDays  // строкой и предыдущего дня больше 2х
                                                .getJSONObject(count - 1)
                                                .getInt("startRow") + 2 < rowIndex
                                ) {
                                    // Если у ячейки верхний борт больше 4х
                                    if (this.getBorderLineValue(rowCellDayWeek, Border.TOP) >= 5) {
                                        // Добавляем день недели
                                        groupSheduleDays.put(new JSONObject()
                                                .put("name", daysWeek[count]) // Имя дня
                                                .put("pairs", new JSONArray()) // Все пары
                                                .put("startRow", rowIndex)); // Номер строки начала дня
                                    // Иначе если у ячейки нижний борт больше 4х
                                    } else if (this.getBorderLineValue(rowCellDayWeek, Border.BOTTOM) >= 5) {
                                        // Добавляем день недели
                                        groupSheduleDays.put(new JSONObject()
                                                .put("name", daysWeek[count]) // Имя дня
                                                .put("pairs", new JSONArray()) // Все пары
                                                .put("startRow", rowIndex + 1)); // Номер строки начала дня + 1
                                    }
                                }
                            }
                            // Если ячейка пустая или её контент с обрезанными пробелами эквивалентен пустой строке
                            if (rowCell == null || rowCell.getContents().trim().equals("")) {
                                int columnIndexCopy = columnIndex; // Копируем индекс столбца группы
                                // Пока ячейка не пустая и её контент пустой и её левый борт не больше нуля
                                // или правый борт уходящей влево итерации ячейки больше нуля
                                while (rowCell != null && rowCell.getContents().equals("") && !(this.getBorderLineValue(rowCell, Border.LEFT) > 0
                                        || this.getBorderLineValue(sheet.getCell(columnIndexCopy - 1, rowIndex), Border.RIGHT) > 0)) {
                                    // Чтение ячейки отступа столбца текущей строки
                                    rowCell = sheet.getCell(columnIndexCopy - 1, rowIndex);
                                    // Чтение ячейки отступа столбца следующей строки
                                    rowCellNext =  sheet.getCell(columnIndexCopy - 1, rowIndex + 1);
                                    columnIndexCopy--; // Уменьшаем отступ
                                }
                                // Если ячейка пустая или её контент пустой или столбец равен первой группе таблицы
                                if (rowCell == null || rowCell.getContents().trim().equals("") ||
                                        columnIndex == 3) continue; // Пропускаем итерацию
                            }

                            // Форматирование строки данных ячейки
                            final String rowCellData = this.getFormattedCell(rowCell);
                            // Вычисление недели ("1", "2", "1|2")
                            final String week = rowIndex % 2 == 0 ? "2" :
                                    (this.getBorderLineValue(rowCell, Border.BOTTOM) == 0 &&
                                            this.getBorderLineValue(rowCellNext, Border.TOP) == 0) ? "1|2" : "1";
                            // Вычисление корпуса (1, 2)
                            final int corpus = rowCell.getCellFormat().getFont().getColour().getDefaultRGB().getRed() > 50 ? 2 : 1;
                            // Получение строки начала отчёта последней недели
                            final JSONObject lastDayObject = groupSheduleDays
                                    .getJSONObject(groupSheduleDays.length() - 1);
                            final int lastDayRowStart = lastDayObject
                                    .getInt("startRow");
                            // Вычисление порядкового номера недели
                            final int dayWeek = groupSheduleDays.length() - (lastDayRowStart <= rowIndex ? 1 : 2);
                            // Вычисление порядкового номера пары в день
                            final int timeN = (int) (rowIndex - groupSheduleDays
                                    .getJSONObject(dayWeek)
                                    .getInt("startRow")) / 2;
                            // Получение объекта времени текущей пары
                            final JSONObject sheduleDay = pairsShedule.getJSONObject(timeN);
                            // Создание строки времени пары в зависимости от корпуса
                            final String time = sheduleDay.getString("timeStart" + corpus) + "-" + sheduleDay.getString("timeEnd" + corpus);
                            final Matcher matcher = patternFIO.matcher(rowCellData); // Прогон строки ячейки через regex
                            String pairName = ""; // Имя пары
                            String cabinet = ""; // Имя кабинета
                            String fio = ""; // ФИО
                            // Если ФИО и группа были найдены
                            if (matcher.find()) {
                                // Вырезаем Группа:ФИО из строки
                                fio = rowCellData.substring(matcher.start());
                                // Если в строке присутствует "," (2 преподователя - 2 кабинета) -
                                // разделение на 2 части и выборка последнего
                                if (fio.contains(",")) fio = fio.substring(fio.indexOf(",") + 1).trim();
                                final Matcher matcherGroup = patternGroup.matcher(fio); // Прогон строки FIO через regex GROUP
                                // Вырезка имени пары
                                pairName = ("" + rowCellData.charAt(0)).toUpperCase() + rowCellData.substring(1, matcher.start() - 1);
                                // Если группа найдена
                                if (matcherGroup.find()) {
                                    // Вырезка имени кабинета
                                    cabinet = matcherGroup.group();
                                    // Вырезка ФИО
                                    fio = fio.substring(matcherGroup.end() + 1);
                                } else {
                                    cabinet = "Error";
                                }
                            // Иначе если содержание ячейки не прошло через regex
                            } else {
                                pairName = rowCellData;
                            }
                            lastDayObject.getJSONArray("pairs").put(new JSONObject()
                                    .put("name", pairName)
                                    .put("cabinet", cabinet)
                                    .put("educator", fio)
                                    .put("time", time)
                                    .put("corpus", corpus)
                                    .put("week", week));
                        }
                    }
                    workbook.close(); // Вызов диструктора excel объекта
                } catch (FileNotFoundException | MalformedURLException | NullPointerException e) {
                    e.printStackTrace();
                } catch (IOException | BiffException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return groupsShedule;
        }
    }
}
