package world.pokeland.cosmetics.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.orlando.pixelchan.repository.Model;
import me.orlando.pixelchan.repository.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileRepository<E extends Model> implements Repository<E> {

    private final File folder;
    private final ObjectMapper mapper;
    private final Class<E> modelClass;

    public FileRepository(File folder, ObjectMapper mapper, Class<E> modelClass) {
        this.folder = folder;
        this.mapper = mapper;
        this.modelClass = modelClass;

        folder.mkdirs();
    }

    @Override
    public boolean saveSync(E entity) {
        File file = new File(folder, entity.id() + ".json");

        try {
            FileWriter fileWriter = new FileWriter(file);
            mapper.writeValue(fileWriter, entity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public E findByIdSync(String id) {
        File file = new File(folder, id + ".json");
        if (!file.exists()) {
            return null;
        }

        try (Reader reader = new FileReader(file)) {
            return mapper.readValue(reader, modelClass);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean existsByIdSync(String id) {
        File file = new File(folder, id + ".json");

        return file.exists();
    }

    @Override
    public List<E> findAllSync() {
        List<E> entities = new ArrayList<>();

        for (File file : folder.listFiles()) {
            try (Reader reader = new FileReader(file)) {
                entities.add(mapper.readValue(reader, modelClass));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return entities;
    }

    @Override
    public boolean deleteByIdSync(String id) {
        File file = new File(folder, id + ".json");

        return file.delete();
    }
}