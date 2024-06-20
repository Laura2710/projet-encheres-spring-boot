export default class PhotoManager {
	constructor() {
		this.previewConteneur=document.getElementById("preview");
		this.inputFile=document.getElementById("inputPhoto");
	}
	
	previewPhoto() {
		this.inputFile.addEventListener("change", () => {
			const file = this.inputFile.files;
			if(file) {
				console.log(file);
            	this.previewConteneur.src=URL.createObjectURL(file[0]);
			}
		});
	}
}