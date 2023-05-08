import 'dart:convert';
import 'package:app/screens/running/running_start.dart';
import 'package:app/widgets/game_result.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

class DailyRunning extends StatefulWidget {
  final String runningStart;
  final String runningEnd;
  final double runningPace;
  final double runningDist;
  final String runningDuration;
  final double runningKcal;

  const DailyRunning(
      {required this.runningStart,
      required this.runningEnd,
      required this.runningPace,
      required this.runningDist,
      required this.runningDuration,
      required this.runningKcal,
      Key? key})
      : super(key: key);

  @override
  State<DailyRunning> createState() => _DailyRunningState();
}

class _DailyRunningState extends State<DailyRunning> {
  List<Map<String, dynamic>> runninglocationList = [];

  Future<void> _loadRunningData(double runningDist) async {
    SharedPreferences runningResult = await SharedPreferences.getInstance();
    SharedPreferences myTodayGoal = await SharedPreferences.getInstance();
    await myTodayGoal.setDouble('now', runningDist);  // 달린 거리 로컬에 저장
    final locationListJson = runningResult.getString('locationList');
    if (locationListJson != null) {
      final locationList = jsonDecode(locationListJson);
      setState(() {
        // runninglocationList = List<Map<String, double>>.from(locationList.map((coord) => {'lat': coord['latitude'], 'lng': coord['longitude']}));
        runninglocationList = List<Map<String, dynamic>>.from(locationList.map((coord) => {'coordinateTime': coord['coordinateTime'], 'lat': coord['latitude'], 'lng': coord['longitude']}));
      });
    }
  }

  @override
  void initState() {
    super.initState();
    _loadRunningData(widget.runningDist);
    sendRunningData(
      runninglocationList,
      widget.runningStart,
      widget.runningEnd,
      widget.runningPace,
      widget.runningDist,
      widget.runningDuration,
      widget.runningKcal,
    );
  }

  void sendRunningData(
      List<Map<String, dynamic>> runninglocationList,
      String runningStart,
      String runningEnd,
      double runningPace,
      double runningDist,
      String runningDuration,
      double runningKcal) async {
    try {
      var dio = Dio();
      print('백에 보낸당!');
      var response = await dio.post('http://k8c107.p.ssafy.io:8081/api/running',
          data: {
            // "coordinateList": [
            //   {
            //     "coordinateTime": "15:40:10",
            //     "lat": 35.2051205,
            //     "lng": 126.8116811
            //   },
            //   {
            //     "coordinateTime": "15:40:30",
            //     "lat": 35.2051147,
            //     "lng": 126.8116459
            //   },
            // ],
            "coordinateList": runninglocationList,
            "memberId": 6,   // *회원 아이디 넣기
            'runningDistance': runningDist,
            "runningEnd": runningEnd,
            'runningKcal': runningKcal,
            'runningPace': runningPace,
            'runningStart': runningStart,
            'runningTime': runningDuration,
          },
          options: Options(
            headers: {
              // 'Authorization': 'Bearer $token',    // *토큰 넣어주기
            },
          ));
      print(response.data);
      // dio 통신이 성공하면 SharedPreferences에서 데이터 제거
      SharedPreferences runningResult = await SharedPreferences.getInstance();
      await runningResult.clear();
    } catch (e) {
      print(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    // 미디어 사이즈
    final mediaWidth = MediaQuery.of(context).size.width;
    final mediaHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      body: SafeArea(
        top: true,
        bottom: false,
        child: Container(
          decoration: BoxDecoration(
              image: DecorationImage(
                  image: AssetImage('assets/images/runningbgi.png'),
                  fit: BoxFit.fitWidth,
                  alignment: Alignment.topLeft,
                  repeat: ImageRepeat.noRepeat)),
          child: Padding(
            padding: EdgeInsets.fromLTRB(mediaWidth * 0.07, mediaHeight * 0.05,
                mediaWidth * 0.07, mediaHeight * 0.02),
            // padding: EdgeInsets.fromLTRB(0, 0, 0, 0),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '나의 달리기 결과',
                      style: TextStyle(
                          fontSize: mediaWidth * 0.08,
                          fontWeight: FontWeight.w700,
                          letterSpacing: 1),
                    ),
                    IconButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => RunningStart()),
                          );
                        },
                        icon: Image.asset('assets/images/closebtn.png'))
                  ],
                ),
                SizedBox(
                  height: mediaHeight * 0.04,
                ),
                Container(
                  width: mediaWidth * 0.7,
                  height: mediaHeight * 0.35,
                  decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(mediaWidth * 0.02),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.5),
                          blurRadius: 28,
                        ),
                      ],
                      image: DecorationImage(
                          // 저장한 경로 이미지? 지도?
                          image: AssetImage('assets/images/running-gif.gif'),
                          fit: BoxFit.fitWidth,
                          alignment: Alignment.topLeft,
                          repeat: ImageRepeat.noRepeat)),
                ),
                SizedBox(
                  height: mediaHeight * 0.05,
                ),
                GameResultInfo(
                  modalType: 'running',
                  runningTime: widget.runningDuration,
                  runningDist: widget.runningDist,
                  runningKcal: widget.runningKcal,
                  runningPace: widget.runningPace,
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
