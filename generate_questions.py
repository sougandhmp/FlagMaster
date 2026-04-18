#!/usr/bin/env python3
"""
Generate 1000 flag quiz questions with country data, facts, and difficulty levels.
"""

import json
import random
from typing import List, Dict

# Comprehensive country data (name, code, difficulty_fact)
COUNTRIES = [
    ("Afghanistan", "AF", "HARD", "Afghanistan is landlocked and known as the 'Graveyard of Empires'."),
    ("Albania", "AL", "HARD", "Albania was the first country to recognize North Korea."),
    ("Algeria", "DZ", "NORMAL", "Algeria is the largest country in Africa by area."),
    ("Andorra", "AD", "HARD", "Andorra is a co-principality with two co-princes."),
    ("Angola", "AO", "NORMAL", "Angola has vast diamond reserves and is located in Southern Africa."),
    ("Antigua and Barbuda", "AG", "HARD", "Antigua and Barbuda consists of two main islands in the Caribbean."),
    ("Austria", "AT", "NORMAL", "Austria is landlocked and famous for classical music composers."),
    ("Azerbaijan", "AZ", "HARD", "Azerbaijan is known as the 'Land of Fire' due to natural gas fires."),
    ("Bahamas", "BS", "NORMAL", "The Bahamas consists of more than 700 islands."),
    ("Bahrain", "BH", "HARD", "Bahrain is an island nation in the Persian Gulf."),
    ("Bangladesh", "BD", "NORMAL", "Bangladesh is one of the most densely populated countries."),
    ("Barbados", "BB", "NORMAL", "Barbados is a tropical island nation in the Caribbean."),
    ("Belarus", "BY", "NORMAL", "Belarus is located in Eastern Europe and borders Russia."),
    ("Belgium", "BE", "NORMAL", "Belgium produces over 220,000 tons of chocolate annually."),
    ("Belize", "BZ", "NORMAL", "Belize has the Great Barrier Reef and is in Central America."),
    ("Benin", "BJ", "HARD", "Benin is located on the western coast of Africa."),
    ("Bhutan", "BT", "HARD", "Bhutan measures progress by GNH (Gross National Happiness)."),
    ("Bolivia", "BO", "NORMAL", "Bolivia is landlocked and home to Lake Titicaca."),
    ("Bosnia and Herzegovina", "BA", "HARD", "Bosnia and Herzegovina has a unique Ottoman heritage."),
    ("Botswana", "BW", "HARD", "Botswana is home to the Kalahari Desert and Okavango Delta."),
    ("Brazil", "BR", "EASY", "Brazil is the largest country in South America."),
    ("Brunei", "BN", "HARD", "Brunei is a small sultanate on the island of Borneo."),
    ("Bulgaria", "BG", "NORMAL", "Bulgaria is noted for its rose oil production."),
    ("Burkina Faso", "BF", "HARD", "Burkina Faso's name means 'Land of the Honest People'."),
    ("Burundi", "BI", "HARD", "Burundi is one of the smallest countries in Africa."),
    ("Cambodia", "KH", "NORMAL", "Cambodia is home to Angkor Wat, the largest religious monument."),
    ("Cameroon", "CM", "NORMAL", "Cameroon is known as 'Africa in miniature' for its diversity."),
    ("Canada", "CA", "EASY", "Canada has the longest coastline of any country."),
    ("Cape Verde", "CV", "HARD", "Cape Verde is an island nation off the coast of Africa."),
    ("Central African Republic", "CF", "HARD", "The Central African Republic is landlocked in central Africa."),
    ("Chad", "TD", "HARD", "Chad is a landlocked country in north-central Africa."),
    ("Chile", "CL", "NORMAL", "Chile is the longest north-south country in the world."),
    ("China", "CN", "EASY", "China is the world's most populous country."),
    ("Colombia", "CO", "NORMAL", "Colombia is the world's leading producer of coffee."),
    ("Comoros", "KM", "HARD", "Comoros is an island nation in the Indian Ocean."),
    ("Congo", "CG", "HARD", "The Congo is rich in natural resources and minerals."),
    ("Costa Rica", "CR", "NORMAL", "Costa Rica is known for biodiversity and ecological tourism."),
    ("Croatia", "HR", "NORMAL", "Croatia has over 1,000 islands along its coast."),
    ("Cuba", "CU", "NORMAL", "Cuba is a Caribbean island nation known for cigars."),
    ("Cyprus", "CY", "HARD", "Cyprus is a Mediterranean island with a divided history."),
    ("Czech Republic", "CZ", "NORMAL", "The Czech Republic leads the world in beer consumption per capita."),
    ("Denmark", "DK", "NORMAL", "Denmark is home to the concept of 'hygge'."),
    ("Djibouti", "DJ", "HARD", "Djibouti is a strategic port in the Horn of Africa."),
    ("Dominica", "DM", "HARD", "Dominica is a volcanic island in the Caribbean."),
    ("Dominican Republic", "DO", "NORMAL", "The Dominican Republic shares the island of Hispaniola with Haiti."),
    ("Ecuador", "EC", "NORMAL", "Ecuador is the only country named after a geographical feature."),
    ("Egypt", "EG", "EASY", "Egypt is home to the ancient pyramids and the Nile River."),
    ("El Salvador", "SV", "NORMAL", "El Salvador is the smallest country in Central America by area."),
    ("Equatorial Guinea", "GQ", "HARD", "Equatorial Guinea is the only Spanish-speaking country in Africa."),
    ("Eritrea", "ER", "HARD", "Eritrea is located in the Horn of Africa on the Red Sea coast."),
    ("Estonia", "EE", "NORMAL", "Estonia is a Baltic nation with advanced digital technology."),
    ("Eswatini", "SZ", "HARD", "Eswatini is a small landlocked kingdom in southern Africa."),
    ("Ethiopia", "ET", "NORMAL", "Ethiopia is the only African country never colonized."),
    ("Fiji", "FJ", "NORMAL", "Fiji is an island nation in the South Pacific Ocean."),
    ("Finland", "FI", "NORMAL", "Finland is known as the land of a thousand lakes."),
    ("France", "FR", "EASY", "France is the most visited country in the world."),
    ("Gabon", "GA", "HARD", "Gabon is one of Africa's most forested countries."),
    ("Gambia", "GM", "HARD", "The Gambia is the smallest country on mainland Africa."),
    ("Georgia", "GE", "NORMAL", "Georgia bridges Europe and Asia and is known for wine."),
    ("Germany", "DE", "EASY", "Germany is Europe's largest economy."),
    ("Ghana", "GH", "NORMAL", "Ghana was the first sub-Saharan African country to gain independence."),
    ("Greece", "GR", "NORMAL", "Greece is the birthplace of Western democracy."),
    ("Grenada", "GD", "HARD", "Grenada is known as the 'Island of Spice'."),
    ("Guatemala", "GT", "NORMAL", "Guatemala has Mayan heritage and Spanish colonial architecture."),
    ("Guinea", "GN", "HARD", "Guinea is located on the west coast of Africa."),
    ("Guinea-Bissau", "GW", "HARD", "Guinea-Bissau is a small country in West Africa."),
    ("Guyana", "GY", "HARD", "Guyana is the only English-speaking country in South America."),
    ("Haiti", "HT", "NORMAL", "Haiti is the most mountainous Caribbean nation."),
    ("Honduras", "HN", "NORMAL", "Honduras is home to Mayan ruins and the Great Barrier Reef."),
    ("Hungary", "HU", "NORMAL", "Hungary is famous for its thermal baths and Danube River."),
    ("Iceland", "IS", "NORMAL", "Iceland is a volcanic island nation in the North Atlantic."),
    ("India", "IN", "EASY", "India is the world's second-most populous country."),
    ("Indonesia", "ID", "NORMAL", "Indonesia is the world's largest archipelago."),
    ("Iran", "IR", "NORMAL", "Iran has one of the world's oldest civilizations."),
    ("Iraq", "IQ", "NORMAL", "Iraq is home to the Tigris and Euphrates rivers."),
    ("Ireland", "IE", "EASY", "Ireland is known as the 'Emerald Isle'."),
    ("Israel", "IL", "NORMAL", "Israel is located in the Middle East on the Mediterranean coast."),
    ("Italy", "IT", "EASY", "Italy is home to the Roman Empire and Renaissance art."),
    ("Ivory Coast", "CI", "NORMAL", "Ivory Coast is the world's largest cocoa producer."),
    ("Jamaica", "JM", "NORMAL", "Jamaica is famous for reggae music and beautiful beaches."),
    ("Japan", "JP", "EASY", "Japan consists of over 6,000 islands."),
    ("Jordan", "JO", "NORMAL", "Jordan is home to the Dead Sea, the lowest point on Earth."),
    ("Kazakhstan", "KZ", "HARD", "Kazakhstan is the world's largest landlocked country."),
    ("Kenya", "KE", "NORMAL", "Kenya is known for its spectacular wildlife and safaris."),
    ("Kiribati", "KI", "HARD", "Kiribati is a Pacific island nation at risk from rising sea levels."),
    ("Korea, North", "KP", "HARD", "North Korea is one of the world's most isolated countries."),
    ("Korea, South", "KR", "NORMAL", "South Korea is a leader in technology and pop culture."),
    ("Kuwait", "KW", "NORMAL", "Kuwait is a major oil-producing nation in the Middle East."),
    ("Kyrgyzstan", "KG", "HARD", "Kyrgyzstan is home to the world's largest walnut forest."),
    ("Laos", "LA", "HARD", "Laos is a landlocked country in Southeast Asia."),
    ("Latvia", "LV", "HARD", "Latvia is a Baltic nation with a rich medieval history."),
    ("Lebanon", "LB", "NORMAL", "Lebanon is known for the Cedar of Lebanon on its flag."),
    ("Lesotho", "LS", "HARD", "Lesotho is entirely surrounded by South Africa."),
    ("Liberia", "LR", "HARD", "Liberia was founded by freed American slaves."),
    ("Libya", "LY", "NORMAL", "Libya is located in North Africa and is mostly desert."),
    ("Liechtenstein", "LI", "HARD", "Liechtenstein is a tiny Alpine principality."),
    ("Lithuania", "LT", "HARD", "Lithuania is a Baltic nation with a unique language."),
    ("Luxembourg", "LU", "HARD", "Luxembourg is one of Europe's wealthiest nations."),
    ("Madagascar", "MG", "NORMAL", "Madagascar is an island nation known for unique wildlife."),
    ("Malawi", "MW", "HARD", "Malawi is known as 'The Warm Heart of Africa'."),
    ("Malaysia", "MY", "NORMAL", "Malaysia is a multicultural nation in Southeast Asia."),
    ("Maldives", "MV", "NORMAL", "Maldives is the world's lowest-lying country."),
    ("Mali", "ML", "NORMAL", "Mali is home to Timbuktu and the Niger River."),
    ("Malta", "MT", "HARD", "Malta is a small Mediterranean island nation."),
    ("Marshall Islands", "MH", "HARD", "The Marshall Islands are a Pacific island nation."),
    ("Mauritania", "MR", "HARD", "Mauritania has vast Saharan desert landscapes."),
    ("Mauritius", "MU", "HARD", "Mauritius is an island nation in the Indian Ocean."),
    ("Mexico", "MX", "EASY", "Mexico is home to ancient Aztec and Mayan civilizations."),
    ("Micronesia", "FM", "HARD", "Micronesia is a Pacific island nation with stone money."),
    ("Moldova", "MD", "HARD", "Moldova is known for wine production and vineyards."),
    ("Monaco", "MC", "HARD", "Monaco is a tiny city-state on the French Riviera."),
    ("Mongolia", "MN", "NORMAL", "Mongolia is a vast steppe country in Central Asia."),
    ("Montenegro", "ME", "HARD", "Montenegro is a small Balkan country known for fjords."),
    ("Morocco", "MA", "NORMAL", "Morocco is known for the Atlas Mountains and deserts."),
    ("Mozambique", "MZ", "NORMAL", "Mozambique is located on the southeast coast of Africa."),
    ("Myanmar", "MM", "NORMAL", "Myanmar is home to thousands of Buddhist temples."),
    ("Namibia", "NA", "HARD", "Namibia is known for the Namib Desert and wildlife."),
    ("Nauru", "NR", "HARD", "Nauru is the second-smallest country by area."),
    ("Nepal", "NP", "NORMAL", "Nepal is home to Mount Everest and the Himalayas."),
    ("Netherlands", "NL", "NORMAL", "The Netherlands is famous for windmills and tulips."),
    ("New Zealand", "NZ", "EASY", "New Zealand granted women voting rights in 1893."),
    ("Nicaragua", "NI", "HARD", "Nicaragua is the largest country in Central America by area."),
    ("Niger", "NE", "HARD", "Niger is a landlocked country in West Africa."),
    ("Nigeria", "NG", "NORMAL", "Nigeria is the most populous country in Africa."),
    ("North Macedonia", "MK", "HARD", "North Macedonia is a Balkan nation with ancient history."),
    ("Norway", "NO", "NORMAL", "Norway is known for fjords, mountains, and northern lights."),
    ("Oman", "OM", "NORMAL", "Oman is located on the southeastern coast of Arabia."),
    ("Pakistan", "PK", "NORMAL", "Pakistan is home to the Karakoram mountain range."),
    ("Palau", "PW", "HARD", "Palau is a Pacific island nation with pristine reefs."),
    ("Palestine", "PS", "HARD", "Palestine is a territory in the Middle East."),
    ("Panama", "PA", "NORMAL", "Panama is home to the famous Panama Canal."),
    ("Papua New Guinea", "PG", "HARD", "Papua New Guinea has incredible biodiversity."),
    ("Paraguay", "PY", "HARD", "Paraguay's flag is unique with different emblems on each side."),
    ("Peru", "PE", "NORMAL", "Peru is home to Machu Picchu and the Andes Mountains."),
    ("Philippines", "PH", "NORMAL", "The Philippines consists of over 7,600 islands."),
    ("Poland", "PL", "NORMAL", "Poland is located in Central Europe and is EU member."),
    ("Portugal", "PT", "NORMAL", "Portugal is the most western country of continental Europe."),
    ("Qatar", "QA", "NORMAL", "Qatar is a wealthy nation in the Persian Gulf."),
    ("Romania", "RO", "NORMAL", "Romania is known for Transylvania and medieval castles."),
    ("Russia", "RU", "EASY", "Russia spans 11 time zones across Eastern Europe and Asia."),
    ("Rwanda", "RW", "HARD", "Rwanda is the 'Land of a Thousand Hills'."),
    ("Saint Kitts and Nevis", "KN", "HARD", "Saint Kitts and Nevis is a dual-island nation."),
    ("Saint Lucia", "LC", "HARD", "Saint Lucia is a volcanic island in the Caribbean."),
    ("Saint Vincent and the Grenadines", "VC", "HARD", "Saint Vincent and the Grenadines is a Caribbean nation."),
    ("Samoa", "WS", "HARD", "Samoa is an island nation in the South Pacific."),
    ("San Marino", "SM", "HARD", "San Marino is an independent city-state in Europe."),
    ("Sao Tome and Principe", "ST", "HARD", "Sao Tome and Principe is an island nation in Africa."),
    ("Saudi Arabia", "SA", "NORMAL", "Saudi Arabia is home to Mecca and has vast oil reserves."),
    ("Senegal", "SN", "NORMAL", "Senegal is located on the western tip of Africa."),
    ("Serbia", "RS", "NORMAL", "Serbia is located in the Balkans in southeastern Europe."),
    ("Seychelles", "SC", "HARD", "Seychelles is an island nation in the Indian Ocean."),
    ("Sierra Leone", "SL", "HARD", "Sierra Leone is located on the west coast of Africa."),
    ("Singapore", "SG", "NORMAL", "Singapore is a city-state and global financial hub."),
    ("Slovakia", "SK", "NORMAL", "Slovakia has caves, mountains, and medieval castles."),
    ("Slovenia", "SI", "NORMAL", "Slovenia is a small Central European country."),
    ("Solomon Islands", "SB", "HARD", "Solomon Islands is a Pacific island nation."),
    ("Somalia", "SO", "HARD", "Somalia is located on the Horn of Africa."),
    ("South Africa", "ZA", "NORMAL", "South Africa has three capitals and great biodiversity."),
    ("South Sudan", "SS", "HARD", "South Sudan is Africa's youngest independent country."),
    ("Spain", "ES", "EASY", "Spain is known for flamenco, paella, and bullfighting."),
    ("Sri Lanka", "LK", "NORMAL", "Sri Lanka is an island nation known for tea and spices."),
    ("Sudan", "SD", "NORMAL", "Sudan is located in northeastern Africa."),
    ("Suriname", "SR", "HARD", "Suriname is the smallest independent country in South America."),
    ("Sweden", "SE", "NORMAL", "Sweden is known for IKEA, meatballs, and Nobel Prizes."),
    ("Switzerland", "CH", "NORMAL", "Switzerland is known for neutrality, watches, and cheese."),
    ("Syria", "SY", "NORMAL", "Syria is located in the Eastern Mediterranean region."),
    ("Taiwan", "TW", "NORMAL", "Taiwan is a self-governing island off the coast of China."),
    ("Tajikistan", "TJ", "HARD", "Tajikistan is a mountainous nation in Central Asia."),
    ("Tanzania", "TZ", "NORMAL", "Tanzania is home to Mount Kilimanjaro and Serengeti."),
    ("Thailand", "TH", "NORMAL", "Thailand is known for temples, beaches, and street food."),
    ("Timor-Leste", "TL", "HARD", "Timor-Leste is located in Southeast Asia."),
    ("Togo", "TG", "HARD", "Togo is a narrow West African nation."),
    ("Tonga", "TO", "HARD", "Tonga is a Pacific island kingdom with unique culture."),
    ("Trinidad and Tobago", "TT", "NORMAL", "Trinidad and Tobago is a Caribbean dual-island nation."),
    ("Tunisia", "TN", "NORMAL", "Tunisia is located on the Mediterranean coast of North Africa."),
    ("Turkey", "TR", "NORMAL", "Turkey bridges Europe and Asia across two continents."),
    ("Turkmenistan", "TM", "HARD", "Turkmenistan has vast natural gas reserves."),
    ("Tuvalu", "TV", "HARD", "Tuvalu is the third-smallest country by population."),
    ("Uganda", "UG", "HARD", "Uganda is located in East Africa on the equator."),
    ("Ukraine", "UA", "NORMAL", "Ukraine is the largest country entirely in Europe."),
    ("United Arab Emirates", "AE", "NORMAL", "UAE is home to the Burj Khalifa and Dubai."),
    ("United Kingdom", "GB", "EASY", "The United Kingdom includes England, Scotland, Wales, and Northern Ireland."),
    ("United States", "US", "EASY", "The United States has 50 states."),
    ("Uruguay", "UY", "NORMAL", "Uruguay is the most developed country in South America."),
    ("Uzbekistan", "UZ", "HARD", "Uzbekistan is located on the historic Silk Road."),
    ("Vanuatu", "VU", "HARD", "Vanuatu is an island nation in the South Pacific."),
    ("Vatican City", "VA", "HARD", "Vatican City is the world's smallest independent state."),
    ("Venezuela", "VE", "NORMAL", "Venezuela is rich in oil reserves."),
    ("Vietnam", "VN", "NORMAL", "Vietnam is known for Ha Long Bay and ancient culture."),
    ("Yemen", "YE", "HARD", "Yemen is located on the Arabian Peninsula."),
    ("Zambia", "ZM", "HARD", "Zambia is home to Victoria Falls."),
    ("Zimbabwe", "ZW", "HARD", "Zimbabwe has ancient stone ruins and wildlife."),
]

def get_random_distractors(country_code: str, num_distractors: int = 3) -> List[tuple]:
    """Get random countries excluding the answer country."""
    available = [c for c in COUNTRIES if c[1] != country_code]
    return random.sample(available, num_distractors)

def generate_question(index: int, country: tuple) -> Dict:
    """Generate a single question for a given country."""
    name, code, base_difficulty, fact = country

    # Get 3 random distractors
    distractors = get_random_distractors(code)

    # Combine and shuffle options
    options = [(name, code)] + [(d[0], d[1]) for d in distractors]
    random.shuffle(options)

    # Determine difficulty - use base difficulty or vary it
    difficulty_roll = random.random()
    if base_difficulty == "EASY":
        difficulty = "EASY" if difficulty_roll < 0.7 else ("NORMAL" if difficulty_roll < 0.9 else "HARD")
    elif base_difficulty == "NORMAL":
        difficulty = random.choice(["EASY", "NORMAL", "HARD"])
    else:  # HARD
        difficulty = "HARD" if difficulty_roll < 0.7 else ("NORMAL" if difficulty_roll < 0.9 else "EASY")

    question = {
        "answer_id": code,
        "countries": [
            {"country_name": opt[0], "country_code": opt[1]}
            for opt in options
        ],
        "country_code": code,
        "difficulty": difficulty,
        "fact": fact
    }

    return question

def generate_1000_questions() -> List[Dict]:
    """Generate 1000 unique questions."""
    questions = []
    used_countries = set()

    # Generate questions cycling through countries multiple times
    attempts = 0
    max_attempts = 5000

    while len(questions) < 1000 and attempts < max_attempts:
        # Pick a random country
        country = random.choice(COUNTRIES)
        code = country[1]

        # Create question
        question = generate_question(len(questions), country)
        questions.append(question)

        attempts += 1

    print(f"Generated {len(questions)} questions in {attempts} attempts")
    return questions

def load_existing_questions(filepath: str) -> List[Dict]:
    """Load existing questions from JSON file."""
    try:
        with open(filepath, 'r') as f:
            data = json.load(f)
            return data.get('questions', [])
    except Exception as e:
        print(f"Error loading existing questions: {e}")
        return []

def save_questions(questions: List[Dict], filepath: str):
    """Save questions to JSON file."""
    data = {"questions": questions}
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"Saved {len(questions)} total questions to {filepath}")

def main():
    filepath = "/Users/sougandhmp/StudioProjects/FlagMaster/app/src/main/assets/questions.json"

    print("Loading existing questions...")
    existing = load_existing_questions(filepath)
    print(f"Found {len(existing)} existing questions")

    print("Generating 1000 new questions...")
    new_questions = generate_1000_questions()

    print(f"Combining: {len(existing)} existing + {len(new_questions)} new = {len(existing) + len(new_questions)} total")
    all_questions = existing + new_questions

    print(f"Saving {len(all_questions)} questions...")
    save_questions(all_questions, filepath)

    # Print statistics
    difficulties = {}
    for q in all_questions:
        diff = q['difficulty']
        difficulties[diff] = difficulties.get(diff, 0) + 1

    print("\nQuestion Statistics:")
    print(f"  Total: {len(all_questions)}")
    for diff in ["EASY", "NORMAL", "HARD"]:
        count = difficulties.get(diff, 0)
        pct = (count / len(all_questions)) * 100
        print(f"  {diff}: {count} ({pct:.1f}%)")

if __name__ == "__main__":
    main()

