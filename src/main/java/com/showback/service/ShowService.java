package com.showback.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.showback.dto.ShowBannerDTO;
import com.showback.dto.ShowDTO;
import com.showback.dto.ShowScheduleDTO;
import com.showback.exception.ShowNotFoundException;
import com.showback.mapper.ShowBannerMapper;
import com.showback.mapper.ShowMapper;
import com.showback.mapper.ShowScheduleMapper;
import com.showback.model.Show;
import com.showback.model.ShowBanner;
import com.showback.model.ShowSchedule;
import com.showback.model.Venue;
import com.showback.repository.ShowBannerRepository;
import com.showback.repository.ShowRepository;
import com.showback.repository.ShowScheduleRepository;
import com.showback.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final VenueRepository venueRepository;

    private final ShowMapper showMapper;
    private final ShowScheduleMapper showScheduleMapper;
    private final ShowBannerMapper showBannerMapper;

    private final ObjectMapper objectMapper;


    @Transactional
    public ShowDTO findShowDTOById(Long showId) throws JsonProcessingException {
        Show foundShow = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException(showId));

        List<ShowScheduleDTO> showScheduleDTOs = foundShow.getShowSchedules().stream()
                .map(showScheduleMapper::toDTO)
                .collect(Collectors.toList());
        List<ShowBannerDTO> showBannerDTOs = foundShow.getShowBanners().stream()
                .map(showBannerMapper::toDTO)
                .collect(Collectors.toList());

        List<String> contentDetail = objectMapper
                .readValue(foundShow.getContentDetail(), new TypeReference<List<String>>() {
        });

        return ShowDTO.builder()
                .showId(foundShow.getShowId())
                .title(foundShow.getTitle())
                .type(foundShow.getType())
                .contentDetail(contentDetail)
                .thumbnailUrl(foundShow.getThumbnailUrl())
                .price(foundShow.getPrice())
                .showSchedules(showScheduleDTOs)
                .showBanners(showBannerDTOs)
                .build();
    }

    @Transactional
    public Show createShow(ShowDTO showDTO) throws JsonProcessingException {

        Venue venue = null;
        if(showDTO.getVenueId() != null) {
            venue = venueRepository.findById(showDTO.getVenueId())
                    .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + showDTO.getVenueId()));
        }

        Show show = showMapper.toEntity(showDTO, venue);

        return showRepository.save(show);
    }

    @Transactional
    public Long updateShow(ShowDTO showDTO) throws JsonProcessingException{
        Show show = showRepository.findById(showDTO.getShowId())
                .orElseThrow(() -> new ShowNotFoundException(showDTO.getShowId()));

        show.setTitle(showDTO.getTitle());
        show.setType(showDTO.getType());

        String contentDetail = objectMapper.writeValueAsString(showDTO.getContentDetail());
        show.setContentDetail(contentDetail);
        show.setThumbnailUrl(showDTO.getThumbnailUrl());
        show.setPrice(showDTO.getPrice());

        if(showDTO.getVenueId() != null){
            Venue venue = venueRepository.findById(showDTO.getVenueId()).orElseThrow(EntityNotFoundException::new);
            show.setVenue(venue);
        }

        if(showDTO.getShowSchedules() != null){

            List<ShowSchedule> scheduleList = showDTO.getShowSchedules().stream()
                    .map(showScheduleMapper::toEntity)
                    .collect(Collectors.toList());
            show.setShowSchedules(scheduleList);
        }

        if(showDTO.getShowBanners() != null){
            List<ShowBanner> showBanners = showDTO.getShowBanners().stream()
                    .map(showBannerMapper::toEntity)
                    .collect(Collectors.toList());
            show.setShowBanners(showBanners);
        }

        return show.getShowId();
    }

}
