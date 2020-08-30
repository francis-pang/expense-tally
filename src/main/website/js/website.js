'use strict';

function tallyExpense( data ) {
  const xmlHttpRequest = new XMLHttpRequest();
  const formData = new FormData();

  // Set up the request
  xmlHttpRequest.open( 'POST', 'https://example.com/cors.php' );

  // Send our FormData object; HTTP headers are set automatically
  XHR.send( formData );

  // Simulate JSON return
  let responseString = "[{\"date\":\"2020-08-02 13:47:00\",\"type\":\"bank\",\"description\":\"BUS/MRT 2511279        SI NG 30OCT\",\"amount\":\"15.96\"},{\"date\":\"2020-08-02 13:50:00\",\"type\":\"bank\",\"description\":\"BUS/MRT 2511277        SI NG 30OCT\",\"amount\":\"99.43\"}]"
  let expenseEntries = JSON.parse($responseString);

  //TODO: Clear the table content

  // Parse the JSON
  const expenseTable = document.getElementById("expenseTable");
  for (let i = 0; i < expenseEntries.length; i++) {
    // date
    if (expenseEntries[i].date) {
      
    }
    // type
    // description
    // amount
  }
}