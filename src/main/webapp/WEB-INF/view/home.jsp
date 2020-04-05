<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Calender Invitation Application</title>
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
              width: auto;
              margin: 10px 10px;
          }
          #events table{
            float: left;
          }
          #events{
            display: inline-block;
            border-top: 1px dashed black;
            padding-top: 10px;
          }
      </style>
  </head>

  <body>
      <div>
          <div>
              <ul>
                <li>You can create new schedule from here</li>
                <li>From date and to date range will be your availability for meeting</li>
                <li>Interval: For how long meeting can be scheduled (ex. 60 min)</li>
                <li>Default time for schedule will be from 9:00 to 17:00 (you can use api for creating event with custom available date,day and time)</li>
              </ul>
          </div>
          <table>
            <tr>
              <th>Title :</th>
              <th><input type="text" id="title"></th>
            </tr>
            <tr>
              <th>Description :</th>
              <th><input type="text" id="description"></th>
            </tr>
            <tr>
              <th>From Date :</th>
              <th><input type="text" id="fromDate" data-format="YYYY/MM/DD" data-template="D MMM YYYY" name="date" value="09-01-2013"></th>
            </tr>
            <tr>
              <th>To Date :</th>
              <th><input type="text" id="toDate" data-format="YYYY/MM/DD" data-template="D MMM YYYY" name="date" value="09-01-2013"></th>
            </tr>
            <tr>
              <th>Interval :</th>
              <th><input type="text" id="interval"> min</th>
            </tr>
            <tr>
              <th>Live :</th>
              <th>
                    <input type="radio" name="live" value="true" checked/> Yes
                    <input type="radio" name="live" value="false" /> No
              </th>
            </tr>
            <tr>
                <th colspan="2"><button onclick="saveEvent()">Save</button></th>
            </tr>
          </table>
      </div>
      <div id="events">
          <div>
            <ul>
              <li>Please publish/live your meeting before geeting url</li>
              <li>Grey box shows event is scheduled</li>
              <li>Event can be be shared only with one person (once scheduled will be marked as completed)</li>
              <li>If a event marked as pause url shared will not work (event can't be scheduled)</li>
            </ul>
          </div>
          <c:forEach var="event" items="${events}" varStatus="loop">
              <jsp:useBean id="fromDate" class="java.util.Date" />
              <jsp:useBean id="toDate" class="java.util.Date" />
              <jsp:setProperty name="fromDate" property="time" value="${event.fromDate}" />
              <jsp:setProperty name="toDate" property="time" value="${event.toDate}" />
              <table style="max-width: 40%; ${!event.active?'background: silver':''}">
                    <tr>
                      <th>Title : </th>
                      <th>${event.title}</th>
                    </tr>
                    <tr>
                      <th>Description : </th>
                      <th>${event.description}</th>
                    </tr>
                    <tr>
                      <th>From Date : </th>
                      <th><fmt:formatDate type = "date" value = "${fromDate}" /></th>
                    </tr>
                    <tr>
                      <th>To Date : </th>
                      <th><fmt:formatDate type = "date" value = "${toDate}" /></th>
                    </tr>
                    <tr>
                      <th>Interval : </th>
                      <th>${event.interval}</th>
                    </tr>
                    <tr>
                      <th>Status : </th>
                      <th>${event.active ? "In Progress":"Completed"}</th>
                    </tr>
                    <tr>
                       <th><button ${!event.active?'disabled':''} onclick="eventAction('${event.url}',${!event.live})">${event.live?"Pause":"Live"}</button></th>
                       <th><button ${(!event.active || !event.live)?'disabled':''} onclick="getUrl('${event.url}')">Get Url</button></th>
                    </tr>
                </table>
          </c:forEach>
      </div>
  </body>

  <script>
     $(function(){
         $('#fromDate').combodate({
            minYear:new Date().getFullYear(),
            maxYear:new Date().getFullYear()+1,
         });
         $('#toDate').combodate({
            minYear:new Date().getFullYear(),
            maxYear:new Date().getFullYear()+1,
         });
     });

     function getUrl(url){
        let origin = window.location.origin;
        alert(origin+"/schedule-event?key="+url);
     }

     function saveEvent(){
        var data = {};
        let title = $("#title").val();
        let description = $("#description").val();
        let fromDate = $("#fromDate").val();
        let toDate = $("#toDate").val();
        let interval = $("#interval").val();
        let live = $("input[name='live']:checked").val()

        if((!title || !description || !fromDate || !toDate || !interval || !live) || (new Date(fromDate) > new Date(toDate))){
            alert("please input valid data");
            return;
        }

        data["title"]=title;
        data["description"]=description;
        data["fromDate"]=new Date(fromDate).getTime();
        data["toDate"]=new Date(toDate).getTime();
        data["live"]=live;
        data["interval"]=interval;

        $.ajax({
            url: '/save/event',
            data: JSON.stringify(data),
            type: 'POST',
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            error: function(response) {
               alert(response.responseJSON.details.join("\n"));
            },
            success: function(data) {
                console.log(data)
                window.location.reload();
            }
        });
     }

     function eventAction(url,action){
        $.ajax({
            url: '/action/event',
            data: JSON.stringify({url:url,action:action}),
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            error: function(response) {
                alert(response.responseJSON.details.join("\n"));
            },
            success: function(data) {
                alert("event updated successfully");
                window.location.reload();
            }
        });
     }
  </script>
</html>