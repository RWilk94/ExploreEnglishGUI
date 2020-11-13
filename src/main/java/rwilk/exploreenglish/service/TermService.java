package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.repository.TermRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TermService {

  private final TermRepository termRepository;

  public TermService(TermRepository termRepository) {
    this.termRepository = termRepository;
  }

  public List<Term> getAll() {
    return termRepository.findAll();
  }

  public Optional<Term> getById(Long id) {
    return termRepository.findById(id);
  }

  public Term save(Term term) {
    return termRepository.save(term);
  }

  public List<Term> saveAll(List<Term> terms) {
    return termRepository.saveAll(terms);
  }

  @Transactional
  public void delete(Term term) {
    termRepository.delete(term);
  }

  @Transactional
  public void deleteById(Long id) {
    termRepository.deleteById(id);
  }

}
