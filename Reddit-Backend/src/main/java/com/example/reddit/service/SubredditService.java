package com.example.reddit.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reddit.dto.SubredditDto;
import com.example.reddit.exceptions.SubredditNotFoundException;
import com.example.reddit.model.Subreddit;
import com.example.reddit.repository.SubredditRepository;

import java.util.List;

import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.save(mapToSubreddit(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException("Subreddit not found with id -" + id));
        return mapToDto(subreddit);
    }

    private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .id(subreddit.getId())
                .postCount(subreddit.getPosts().size())
                .build();
    }

    private Subreddit mapToSubreddit(SubredditDto subredditDto) {
        return Subreddit.builder().name("/r/" + subredditDto.getName())
                .description(subredditDto.getDescription())
                .user(authService.getCurrentUser())
                .createdDate(now()).build();
    }
}
