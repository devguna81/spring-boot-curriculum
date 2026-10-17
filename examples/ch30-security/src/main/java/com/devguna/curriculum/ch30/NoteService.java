package com.devguna.curriculum.ch30;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final Map<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Note add(String owner, String text) {
        Note note = new Note(sequence.incrementAndGet(), owner, text);
        notes.put(note.id(), note);
        return note;
    }

    public List<Note> ownedBy(String owner) {
        return notes.values().stream().filter(n -> n.owner().equals(owner)).sorted(java.util.Comparator.comparingLong(Note::id)).toList();
    }

    public Note get(long id) {
        Note note = notes.get(id);
        if (note == null) {
            throw new NoSuchElementException("note " + id);
        }
        return note;
    }

    /**
     * 본인 메모이거나 관리자만 지울 수 있다. URL 규칙으로는 표현할 수 없는 "데이터에 따른" 권한이라
     * 메서드 보안으로 건다. 표현식의 @noteService.isOwner는 이 빈의 메서드를 부른다.
     */
    @PreAuthorize("hasRole('ADMIN') or @noteService.isOwner(#id, authentication.name)")
    public void delete(long id) {
        notes.remove(id);
    }

    public boolean isOwner(long id, String username) {
        Note note = notes.get(id);
        return note != null && note.owner().equals(username);
    }
}
