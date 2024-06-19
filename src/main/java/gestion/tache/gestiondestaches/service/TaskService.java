package gestion.tache.gestiondestaches.service;

import gestion.tache.gestiondestaches.model.Task;
import gestion.tache.gestiondestaches.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        if (task.getName() == null || task.getName().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la tâche ne peut pas être nul ou vide");
        }
        if (task.getName().length() > 50) {
            throw new IllegalArgumentException("Le nom de la tâche ne peut pas dépasser 50 caractères");
        }
        if (task.getDescription() != null && task.getDescription().length() > 200) {
            throw new IllegalArgumentException("La description de la tâche ne peut pas dépasser 200 caractères");
        }
        if (task.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être passée");
        }
        if (task.getEndDate() != null && task.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être passée");
        }
        if (taskRepository.findByName(task.getName()).isPresent()) {
            throw new IllegalArgumentException("Tâche avec le même nom déjà existante");
        }
        if (task.getEndDate() != null && task.getStartDate() != null && task.getEndDate().isBefore(task.getStartDate())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        if (task.getCompletionDate() != null && task.getStartDate() != null && task.getCompletionDate().isBefore(task.getStartDate())) {
            throw new IllegalArgumentException("La date de réalisation ne peut pas être antérieure à la date de début");
        }
        if ("completed".equals(task.getStatus().toLowerCase())) {
            if (task.getEndDate() == null) {
                throw new IllegalArgumentException("La tâche terminée doit avoir une date de fin");
            }
            if (task.getCompletionDate() == null) {
                throw new IllegalArgumentException("La tâche terminée doit avoir une date d’achèvement");
            }
        }
        if (!"facile".equalsIgnoreCase(task.getDifficulty()) &&
                !"moyenne".equalsIgnoreCase(task.getDifficulty()) &&
                !"difficile".equalsIgnoreCase(task.getDifficulty())) {
            throw new IllegalArgumentException("La difficulté doit être 'facile', 'moyenne' ou 'difficile'");
        }

        // Ajouter 2 jours à la date de fin si la tâche est difficile
        if ("difficile".equalsIgnoreCase(task.getDifficulty()) && task.getEndDate() != null) {
            task.setEndDate(task.getEndDate().plusDays(2));
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tâche introuvable"));
        if (taskDetails.getEndDate() != null && taskDetails.getStartDate() != null && taskDetails.getEndDate().isBefore(taskDetails.getStartDate())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        if (taskDetails.getCompletionDate() != null && taskDetails.getStartDate() != null && taskDetails.getCompletionDate().isBefore(taskDetails.getStartDate())) {
            throw new IllegalArgumentException("La date de réalisation ne peut pas être antérieure à la date de début");
        }
        if (!"facile".equalsIgnoreCase(taskDetails.getDifficulty()) &&
                !"moyenne".equalsIgnoreCase(taskDetails.getDifficulty()) &&
                !"difficile".equalsIgnoreCase(taskDetails.getDifficulty())) {
            throw new IllegalArgumentException("La difficulté doit être 'facile', 'moyenne' ou 'difficile'");
        }

        existingTask.setName(taskDetails.getName());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setStartDate(taskDetails.getStartDate());
        existingTask.setCompletionDate(taskDetails.getCompletionDate());
        existingTask.setStatus(taskDetails.getStatus());
        existingTask.setDifficulty(taskDetails.getDifficulty());

        // Ajouter 2 jours à la date de fin si la tâche est difficile
        if ("difficile".equalsIgnoreCase(taskDetails.getDifficulty()) && taskDetails.getEndDate() != null) {
            existingTask.setEndDate(taskDetails.getEndDate().plusDays(2));
        } else {
            existingTask.setEndDate(taskDetails.getEndDate());
        }

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tâche introuvable"));
        taskRepository.delete(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
}
