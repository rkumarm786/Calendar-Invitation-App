<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Schedule Calender Invitation</title>
      <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
      <script src="https://vitalets.github.io/combodate/combodate.js"></script>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment.js"></script>
      <style>
          th, tr {
              border: 1px solid;
              width: 20%;
              padding: 5px 5px;
          }
          table {
            width: 50%;
            margin: auto;
          }
          ul{
              list-style: none;
              padding-left: 0px;
          }
      </style>
  </head>
  <body>

    <div>
      <div>
          <ul>
            <li><i>You can book slot from <b id="fromDate"></b> to <b id="toDate"></b></i></li>
            <li>Select date in range from and to date shown</li>
            <li>Select valid date to choose time slots</li>
          </ul>
      </div>
      <table>
          <tr>
            <th>Title :</th>
            <th id="title">${schedule.title}</th>
          </tr>
          <tr>
            <th>Description :</th>
            <th id="description">${schedule.description}</th>
          </tr>
          <tr>
            <th>Slot :</th>
            <th><span id="interval"></span> min</th>
          </tr>
          <tr>
              <th>Select Date : </th>
              <th><input onchange="updateTimeSlot(event)" type="text" id="scheduleDate" data-format="MM/DD/YYYY" data-template="D MMM YYYY" name="date"></th>
          </tr>
          <tr>
              <th>Select Time Slot : </th>
              <th>
                  <ul id="time-slots">
                  </ul>
              </th>
          </tr>
          <tr>
            <th>Your Name :</th>
            <th><input type="text" id="name"></th>
          </tr>
          <tr>
            <th>Your Email :</th>
            <th><input type="email" id="email"></th>
          </tr>
          <tr>
              <th colspan="2"><button onclick="scheduleSlot()">Schedule Meeting</button></th>
          </tr>
      </table>
    </div>
  </body>
  <script>
    var scheduleData = null;
    var data = {};

    $(function(){
         $('#scheduleDate').combodate({
            minYear:new Date().getFullYear()-1,
            maxYear:new Date().getFullYear()+1,
         });

         $.ajax({
              url: '/schedule-data?key='+new URLSearchParams(window.location.search).get("key"),
              type: 'GET',
              dataType: "json",
              contentType: "application/json;charset=utf-8",
              error: function(response) {
                  alert(response.responseJSON.details.join("\n"));
              },
              success: function(data) {
                  scheduleData = data;
                  $("#fromDate").html(new Date(scheduleData.fromDate).toDateString());
                  $("#toDate").html(new Date(scheduleData.toDate).toDateString());
                  $("#title").html(scheduleData.title);
                  $("#description").html(scheduleData.description);
                  $("#interval").html(scheduleData.interval);
              }
         });
     });

     function updateTimeSlot(event){
        let date = event.target.value;
        if(date){
            let millis = new Date(date).getTime();
            data["date"]=millis;

            if(!(millis>=scheduleData.fromDate && millis<=scheduleData.toDate)){
                alert("please select valid date");
                return;
            }
            console.log(scheduleData);
            let day = dayOfWeekAsString(new Date(date).getDay());
            
            let customDayData = scheduleData.customDay[day];
            let customDateData = scheduleData.customDay[millis];
            
            let hour = Math.floor(scheduleData.interval/60);
            let minutes = scheduleData.interval%60;
            
            let minsList = [];

            if(customDateData && customDateData.length>0){
              addTimeSlots(customDateData,minsList);
            }else if(customDayData && customDayData.length>0){
              addTimeSlots(customDayData,minsList);
            }
            
            let unavailableSlots = scheduleData.unavailableTimeSlots[millis];
            if(unavailableSlots && unavailableSlots.length>0){
              for(let i=0;i<unavailableSlots.length;i++){
                  if(minsList.indexOf(unavailableSlots[i]["from"])>=0){
                    minsList.splice(minsList.indexOf(unavailableSlots[i]["from"]),1);
                  }
                }
            }
            if(minsList.length==0){
              $("#time-slots").html("no time slot available for this date");
            }else{
              displayTimeSlots(minsList);
            }
        }
     }

    function displayTimeSlots(minsList){
      let listHtml="";
      for(let i=0;i<minsList.length;i++){
          let time = pad(Math.floor(minsList[i]/60))+":"+pad(minsList[i]%60);
          listHtml+='<li><input type="radio" name="scheduleTime" value="'+time+'"/> '+time+' </li>';
      }
      $("#time-slots").html(listHtml);
    }

    function scheduleSlot(){
        let time = $("input[name='scheduleTime']:checked").val();
        if(!time){
          alert("Select time from given slots");
          return;
        }
        let timeSec = parseInt(time.split(":")[0])*60+parseInt(time.split(":")[1]);
       
        data["name"]=$("#name").val();
        data["email"]=$("#email").val();
        data["fromTime"]=timeSec;
        data["toTime"]=timeSec+scheduleData.interval;
        data["url"]=new URLSearchParams(window.location.search).get("key");
        
        if(!data["name"] || !data["email"] || !data["fromTime"] || !data["toTime"] ||!data["url"] ||!data["date"]){
            alert("please provide all details");
            return;
        }

        $.ajax({
            url: '/schedule-meeting',
            data: JSON.stringify(data),
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            error: function(response) {
                alert(response.responseJSON.details.join("\n"));
            },
            success: function(data) {
                alert("meeting scheduled successfully");
                window.location.reload;
            }
        });
    }

    function addTimeSlots(customData,minsList){
      for(let i=0;i<customData.length;i++){
          let min = customData[0]["from"];
          let max = customData[0]["to"];
          
          while(min<max){
            minsList.push(min);
            min += scheduleData.interval;
          }
      }
    }

     function pad(n) {
          return (n < 10) ? ("0" + n) : n;
     }

     function dayOfWeekAsString(dayIndex) {
      return ["SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"][dayIndex];
     }

  </script>
</html>