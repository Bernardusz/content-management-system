package io.github.bernardusz.cms.content;

import io.github.bernardusz.cms.content.dto.ContentCreation;
import io.github.bernardusz.cms.content.dto.ContentDetail;
import io.github.bernardusz.cms.content.dto.ContentSummary;
import io.github.bernardusz.cms.content.dto.ContentUpdate;
import io.github.bernardusz.cms.exception.exceptions.ContentNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {
  private final ContentRepository contentRepository;
  public ContentService(ContentRepository contentRepository){
    this.contentRepository = contentRepository;
  }

  @Transactional
  public Optional<Long> save(ContentCreation contentCreation){
    return contentRepository.save(contentCreation);
  }

  @Transactional(readOnly = true)
  public List<ContentSummary> findAllWithFilter(String identifier, int limit, int offsets){
    return contentRepository.findAllWithFilter(identifier, limit, offsets);
  }

  @Transactional(readOnly = true)
  public ContentDetail findById(Long id, Long userId){
    return contentRepository.findById(id, userId).orElseThrow(
        () -> new ContentNotFound("Content isn't found")
    );
  }

  @Transactional
  public void updateById(Long id, ContentUpdate contentUpdate){
    contentRepository.updateById(id, contentUpdate);
  }

  @Transactional
  public void deleteById(Long id){
    contentRepository.deleteById(id);
  }

  @Transactional
  public void increaseLike(Long contentId, Long userId){
    contentRepository.increaseLike(contentId, userId);
  }

  @Transactional
  public void decreaseLike(Long contentId, Long userId){
    contentRepository.decreaseLike(contentId, userId);
  }

  @Transactional
  public void increaseDislike(Long contentId, Long userId){
    contentRepository.increaseDislike(contentId, userId);
  }

  @Transactional
  public void decreaseDislike(Long contentId, Long userId){
    contentRepository.decreaseDislike(contentId, userId);
  }
}
