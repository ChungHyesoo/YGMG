package com.ygmg.running.service;

import com.ygmg.running.dto.RunningCoordinateResponse;
import com.ygmg.running.dto.RunningListResponse;
import com.ygmg.running.dto.RunningRequest;
import com.ygmg.running.dto.RunningResponse;
import com.ygmg.running.entity.Mode;
import com.ygmg.running.entity.Running;
import com.ygmg.running.entity.RunningCoordinate;
import com.ygmg.running.entity.RunningDetail;
import com.ygmg.running.repository.RunningDetailRepository;
import com.ygmg.running.repository.RunningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RunningServiceImpl implements RunningService{

    private final RunningRepository runningRepository;

    private final RunningDetailRepository runningDetailRepository;

    @Override
    public void saveRunningRecord(RunningRequest runningRequest) {


        List<RunningCoordinate> list = new ArrayList<>();

        for(RunningRequest.Coordinate coordinate : runningRequest.getCoordinateList()){
            list.add(RunningCoordinate.builder()
                            .runningLat(coordinate.getLat())
                            .runningLng(coordinate.getLng())
                            .coordinateTime(coordinate.getCoordinateTime())
                            .build());
        }

        RunningDetail runningDetail = RunningDetail.builder()
                .runningStart(runningRequest.getRunningStart())
                .runningEnd(runningRequest.getRunningEnd())
                .runningKcal(runningRequest.getRunningKcal())
                .runningDistance(runningRequest.getRunningDistance())
                .runningPace(runningRequest.getRunningPace())
                .runningTime(Time.valueOf(runningRequest.getRunningTime()))
                .runningMode(Mode.RUNNING)
                .runningCoordinateList(list)
                .build();


        Running running = Running.builder()
                .runningDate(runningRequest.getRunningStart().toLocalDate())
                .memberId(runningRequest.getMemberId())
                .runningDetail(runningDetail)
                .build();

        runningRepository.save(running);
    }

    @Override
    public RunningResponse selectRunningDetail(Long runningId) {
        Running running = runningRepository.findById(runningId).get();

        RunningResponse runningResponse = RunningResponse.builder()
                .runningDetailId(running.getRunningDetail().getId())
                .runningStart(running.getRunningDetail().getRunningStart())
                .runningEnd(running.getRunningDetail().getRunningEnd())
                .runningKcal(running.getRunningDetail().getRunningKcal())
                .runningDistance(running.getRunningDetail().getRunningDistance())
                .runningPace(running.getRunningDetail().getRunningPace())
                .runningTime(running.getRunningDetail().getRunningTime().toLocalTime())
                .runningMode(running.getRunningDetail().getRunningMode().toString())
                .build();

        return runningResponse;
    }

    @Override
    public RunningListResponse selectRunningList(Long memberId) {

        List<Running> runningList = runningRepository.findAllByMemberId(memberId);

        RunningListResponse runningListResponse = new RunningListResponse();
        List<RunningListResponse.RunningDto> runningDtoList = new ArrayList<>();
        runningListResponse.setMemberId(memberId);
        for(Running running : runningList){

            RunningListResponse.RunningDto runningDto = RunningListResponse.RunningDto.builder()
                    .runningDate(running.getRunningDate())
                    .runningId(running.getId())
                    .build();

            runningDtoList.add(runningDto);
        }
        runningListResponse.setRunningList(runningDtoList);


        return runningListResponse;

    }

    @Override
    public RunningCoordinateResponse selectRunningCoordinate(Long runningDetailId) {


        RunningCoordinateResponse runningCoordinateResponse = new RunningCoordinateResponse();

        runningCoordinateResponse.setRunningDetailId(runningDetailId);

        List<RunningCoordinateResponse.RunningCoordinateDto> runningCoordinateDtoList = new ArrayList<>();

        RunningDetail runningDetail = runningDetailRepository.findById(runningDetailId).get();
        for(RunningCoordinate runningCoordinate : runningDetail.getRunningCoordinateList()){

            RunningCoordinateResponse.RunningCoordinateDto runningCoordinateDto = RunningCoordinateResponse.RunningCoordinateDto.builder()
                    .runningLat(runningCoordinate.getRunningLat())
                    .runningLng(runningCoordinate.getRunningLng())
                    .coordinateTime(runningCoordinate.getCoordinateTime())
                    .build();

            runningCoordinateDtoList.add(runningCoordinateDto);
        }

        runningCoordinateResponse.setRunningCoordinateList(runningCoordinateDtoList);


        return runningCoordinateResponse;
    }
}
