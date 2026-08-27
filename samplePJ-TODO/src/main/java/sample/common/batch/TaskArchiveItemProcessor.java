package sample.common.batch;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import sample.common.dao.entity.Task;

@Component
public class TaskArchiveItemProcessor implements ItemProcessor<Task, Task> {
    
    @Override
    public Task process(Task task) throws Exception {
        if (!"完了".equals(task.getStatus())) {
            return null; // 対象外なら、Writerに渡さずスキップ
        }
        return task;
    }
}