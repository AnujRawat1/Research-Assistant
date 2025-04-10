document.addEventListener('DOMContentLoaded', () => {

    chrome.storage.local.get(['researchNotes'], function(result) {
       if (result.researchNotes) {
        document.getElementById('notes').value = result.researchNotes;
       } 
    });

    document.getElementById('processBtn').addEventListener('click', summarizeText);
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotes);

    document.getElementById('copyNotesBtn').addEventListener('click', function() {
        copyClipboard('notes');
    });

    document.getElementById('downloadNotesBtn').addEventListener('click', function() {
        downloadNotes('notes');
    });


});


async function summarizeText() {
    try {
        const [tab] = await chrome.tabs.query({ active:true, currentWindow: true});
        const [{ result }] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () => window.getSelection().toString()
        });
        
        const customPrompt = document.getElementById('custom-prompt').value;
        const checkPrompt = customPrompt.trim() === ""  || customPrompt == null ;

        if (!result && checkPrompt) {
            let noContentError = 'Please select some text first or Add Some Prompt';
            document.getElementById('results').innerHTML = `<div class="result-item"><div class="result-content"><b style="color: crimson;"> ${noContentError}</b></div> </div>`;
            return;
        }
        
        
        const operation = document.getElementById('operation').value;

        const response = await fetch('https://research-assistant-daay.onrender.com/api/research/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: result || customPrompt , operation: operation })
        });

        if (!response.ok) {
            throw new Error(`API Error: ${response.status}`);
        }

        const text = await response.text();
        showResult(text.replace(/\n/g,'<br>'));

    } catch (error) {
        showResult('Error: ' + error.message);
    }
}


async function saveNotes() {
    const notes = document.getElementById('notes').value;
    chrome.storage.local.set({ 'researchNotes': notes}, function() {
        alert('Notes saved successfully');
    });
}


function showResult(content) {
    document.getElementById('results').innerHTML = `
    <div class="result-item"> 

        <h2>Research Content</h2> 
        <div id="result-content">${content}</div> 

        <div class="functionaity">
            <button id="copyResearchBtn">Copy</button>
            <button id="downloadResearchBtn">Download</button>
        </div>  

    </div>`;

    document.getElementById('results').scrollIntoView({ behavior: 'smooth' });


    document.getElementById('copyResearchBtn').addEventListener('click', function() {
        copyClipboard('result-content');
    });
      
    document.getElementById('downloadResearchBtn').addEventListener('click', function() {
        downloadNotes('result-content');
    });
}


function copyClipboard(id) {
    const element = document.getElementById(id);
    if (!element) return console.error("Element not found!");

    const text = element.value || element.innerText;

    navigator.clipboard.writeText(text)
        .then(() => {
            alert("Successfully Copied!");
        })
        .catch(err => {
            console.error("Failed to copy: ", err);
        });
}



function downloadNotes(id) {
    const element = document.getElementById(id);
    if (!element) return console.error("Element not found!");

    const text = element.value || element.innerText;
    const blob = new Blob([text], { type: "text/plain" });

    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "ResearchNotes.txt";
    link.click();

    URL.revokeObjectURL(link.href);
}

