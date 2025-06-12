package ru.kermort.praktikum.taskmanager;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.kermort.praktikum.taskmanager.manager.FileBackedTaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(FileBackedTaskManager.DATE_FORMATTER));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.NULL) {
            return LocalDateTime.parse(jsonReader.nextString(), FileBackedTaskManager.DATE_FORMATTER);
        } else {
            jsonReader.nextNull();
            return null;
        }
    }
}
