package sample.common.batch;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import org.springframework.stereotype.Component;

import sample.common.dao.entity.Task;
import sample.common.dao.mapper.TaskArchiveMapper;

@Component
public class TaskArchiveItemWriter implements ItemWriter<Task> {
    
    private final TaskArchiveMapper taskArchiveMapper;

    public TaskArchiveItemWriter(TaskArchiveMapper taskArchiveMapper) {
        this.taskArchiveMapper = taskArchiveMapper;
    }

    @Override
    public void write(Chunk<? extends Task> chunk) throws Exception {
        for (Task task : chunk.getItems()) {
            taskArchiveMapper.insertArchive(task);
            taskArchiveMapper.deleteById(task.getId());
        }
    }
}
