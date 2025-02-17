package com.actiangent.wasteye.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.actiangent.wasteye.R

enum class Waste(
    @StringRes val nameResId: Int,
    @DrawableRes val imageResId: Int,
    val description: String,
    val explanation: String,
    val sortation: String,
    val recycling: String,
) {
    CARDBOARD(
        nameResId = R.string.waste_type_cardboard,
        imageResId = R.drawable.thumbnail_waste_cardboard,
        description = "Cardboard is a paper-based material used for packaging and packing goods. Cardboard is usually made from recycled fibre and has high durability. Although recyclable, cardboard exposed to oil or water is difficult to reprocess. Proper management of cardboard includes dry storage and shredding before sending it to recycling centres.",
        explanation = "Cardboard is a paper-based material that is often used for packing and packaging goods. Usually made from recycled fibre, cardboard has good strength and durability. However, cardboard contaminated with oil, water, or other chemicals becomes difficult to recycle as the fibres are damaged and the recycling process is hampered.",
        sortation = "Separate cardboard from other waste, especially contaminated ones. Make sure the cardboard is dry and clean. Fold or cut the cardboard into smaller pieces for easier storage and transport.",
        recycling = "Clean cardboard can be recycled into new paper products. The recycling process involves crushing the cardboard into fibres, which are then processed into new sheets of paper. For cardboard that cannot be recycled due to contamination, consider using it as an alternative fuel or compost material, although the efficiency may be lower."
    ),
    GLASS(
        nameResId = R.string.waste_type_glass,
        imageResId = R.drawable.thumbnail_waste_glass,
        description = "Glass is a material derived from silica sand, soda, and lime, which is used in bottles, glasses, and windows. Glass can be recycled many times without loss of quality, but must be sorted by colour. Broken glass poses a risk of injury and is difficult to process when mixed with other materials. For proper management, glass should be cleaned of food debris and stored in a separate container before recycling.",
        explanation = "Glass is an inorganic material made of silica sand, soda, and lime, used in various products such as bottles, cups, and windows. Glass can be recycled indefinitely without loss of quality, but the recycling process requires separation by colour (clear, green, brown) due to differences in chemical composition. Broken glass can pose a physical hazard and complicate the recycling process if mixed with other materials.",
        sortation = "Separate glass by colour and type. Avoid mixing glass with other materials such as metal or plastic. Make sure to handle broken glass with care to avoid injury.",
        recycling = "Glass that has been sorted and cleaned is melted at high temperatures and then reshaped into new products such as bottles or other containers. This process saves energy and resources compared to glass production from raw materials."
    ),
    METAL(
        nameResId = R.string.waste_type_metal,
        imageResId = R.drawable.thumbnail_waste_metal,
        description = "Metal waste such as aluminium and iron comes from beverage cans, food, or vehicle parts. Metals have a high recycling value because they can be melted down and reshaped into new products. Aluminium cans, for example, can be recycled in a short time into new cans. For sorting, metals must be separated from plastic or paper, and rusted or contaminated metals are more difficult to recycle.",
        explanation = "Metals, such as aluminium and iron are often found in the form of beverage cans, food, or vehicle parts. Metals have a high recycling value as they can be melted down and remoulded into new products without any loss of quality. However, metals that are corroded or contaminated by other materials require additional cleaning processes before they can be recycled.",
        sortation = "Separate metals by type, such as aluminium and iron, as they have different recycling processes. Clean the metals from food debris or other materials to facilitate the recycling process.",
        recycling = "The sorted and cleaned metals are melted down in special furnaces and then moulded into new products. For example, aluminium cans can be recycled into new cans or other aluminium products, saving energy and natural resources."
    ),
    PAPER(
        nameResId = R.string.waste_type_paper,
        imageResId = R.drawable.thumbnail_waste_paper,
        description = "Paper is derived from wood fibres and is used in a variety of products such as newspapers, magazines, and packaging. Clean paper can be recycled up to 5-7 times before the fibres weaken. However, paper that has been exposed to oil, food or wax cannot be recycled. To sort it, make sure the paper is dry and not mixed with non-paper materials such as plastic or metal.",
        explanation = "Paper is a material made from wood fibres and is used in a variety of products such as newspapers, magazines, and packaging. Clean paper can be recycled up to several times before the fibres become too short to reuse. However, paper contaminated with oil, food or other chemicals cannot be recycled as it can damage the recycling process and the quality of the final product.",
        sortation = "Separate clean paper from contaminated paper. Clean paper includes newspapers, magazines, and office paper, while contaminated paper includes used tissues, greaseproof paper, or plastic-coated paper.",
        recycling = "Clean paper is crushed into fibres, mixed with water to form pulp, and then processed into new sheets of paper. Paper that cannot be recycled can be used as compostable material or alternative fuel, although its contribution may be smaller than that of recycling."
    ),
    PLASTIC(
        nameResId = R.string.waste_type_plastic,
        imageResId = R.drawable.thumbnail_waste_plastic,
        description = "Plastic is made from synthetic polymers used in bottles, food packaging, and household appliances. Plastics come in many types that must be separated based on recycling codes. Some plastics can be recycled into new products, but many end up in landfills or polluting the environment. Clean plastics are easier to recycle, so wash them before throwing them in the recycling bin.",
        explanation = "Plastic is a synthetic material made from polymers and is used in various products such as beverage bottles, food packaging, household appliances, and industrial components. Plastic comes in different types with different properties and recycling rates. Each type of plastic is assigned a recycling code to aid the sorting and processing process. However, not all recycling facilities accept all types of plastic as some types are more difficult to process due to certain composition or contamination. Some commonly used plastic types include PET (Polyethylene Terephthalate) which is found in many beverage bottles and food packaging, and HDPE (High-Density Polyethylene) which is used for milk bottles, detergent packaging, and plastic pipes. Both types of plastic have high recycling rates and are often turned into new bottles, textiles or building materials. Meanwhile, PVC (Polyvinyl Chloride) used in water pipes, vinyl flooring, and toys, is more difficult to recycle as it contains harmful additives. LDPE (Low-Density Polyethylene) is often used for plastic bags, food wrappers and flexible bottles, but its recycling rate is low although it can be processed into garbage bags or building materials. PP (Polypropylene) plastic found in yogurt containers, bottle caps, and household appliances can be recycled into various hard plastic products, but the process is more difficult than PET and HDPE. Meanwhile, PS (Polystyrene) or better known as Styrofoam is widely used for disposable food containers and building insulation, but is difficult to recycle and often ends up in landfills. There is also the Other category which includes various types of plastics such as PC (Polycarbonate) which is often used for baby bottles and gallon water containers, as well as bioplastics such as PLA which can biodegrade in industrial composting facilities.",
        sortation = "Separate plastics by type and recycling code. Clean the plastic from food residue or other materials to prevent contamination that can hinder the recycling process.",
        recycling = "The sorted and cleaned plastic is shredded into small flakes, then melted and moulded into plastic pellets. These pellets can be used as raw materials for new plastic products. Some types of plastics that are difficult to recycle can be processed into alternative fuels through pyrolysis process or other products such as paving blocks, although this method is still under development and has limited application."
    ),
    UNKNOWN(
        nameResId = R.string.waste_type_unknown,
        imageResId = R.drawable.baseline_question_mark_24,
        description = "Unknown",
        explanation = "",
        sortation = "",
        recycling = ""
    );

}