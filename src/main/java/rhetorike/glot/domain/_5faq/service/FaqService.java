package rhetorike.glot.domain._5faq.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._5faq.dto.FaqDto;
import rhetorike.glot.domain._5faq.entity.Faq;
import rhetorike.glot.domain._5faq.repository.FaqRepository;
import rhetorike.glot.domain._5faq.repository.QFaqRepository;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final QFaqRepository qFaqRepository;

    @Transactional
    public SingleParamDto<Long> createFaq(FaqDto.CreationRequest requestDto) {
        Faq faq = Faq
                .builder()
                .type(requestDto.getType())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        faqRepository.save(faq);
        return new SingleParamDto<>(faq.getId());
    }

    @Transactional
    public void updateFaq(Long id, FaqDto.UpdateRequest requestDto) {
        Faq faq = faqRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        faq.update(requestDto);
    }

    @Transactional
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    @Transactional
    public Integer getPageCount(int size) {
        return Math.toIntExact(Math.min(0, faqRepository.count() - 1) / size);
    }

    @Transactional
    public FaqDto.Response findFaq(Long id) {
        Faq faq = faqRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new FaqDto.Response(faq);
    }

    @Transactional
    public FaqDto.PageResponse findAllFaq(FaqDto.GetRequest requestDto) {
        Page<Faq> faqPage = qFaqRepository.findAllFaq(requestDto);
        List<FaqDto.Response> faqs = faqPage
                .stream()
                .map(FaqDto.Response::new)
                .toList();

        return new FaqDto.PageResponse(faqPage.getTotalPages(), faqs);
    }

    @Transactional
    public List<FaqDto.Response> findAllFag() {
        return faqRepository
                .findAll()
                .stream()
                .map(FaqDto.Response::new)
                .toList();
    }
}
